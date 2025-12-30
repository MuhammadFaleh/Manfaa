from typing import List
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import ChatBot.model as model
import API.request as request
import API.response as response
import ChatBot.ingest as ingest
import json

llm_rag = model.RAGApplication(model.rag_chain)
llm_suggest_hours = model.hours_chain
matcher = ingest.BidMatcher()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)


@app.post("/ask-rag", response_model=response.QueryResponse)
async def query_rag(data: request.QueryRequest):
    try:
        if not data.question or not data.question.strip():
            raise HTTPException(status_code=400, detail="Question cannot be empty")

        answer = llm_rag.run(data.question)

        if not answer:
            raise HTTPException(status_code=500, detail="Failed to generate answer")

        return {"answer": answer}

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Internal error: {str(e)}")


@app.post("/suggest-hours", response_model=response.EstimateHours)
async def suggest_hours(data: request.EstimateHours):
    try:
        if not data.category or not data.category.strip():
            raise HTTPException(status_code=400, detail="Category cannot be empty")

        if not data.description or not data.description.strip():
            raise HTTPException(status_code=400, detail="Description cannot be empty")

        if not data.deliverables or not data.deliverables.strip():
            raise HTTPException(status_code=400, detail="Deliverables cannot be empty")

        result = llm_suggest_hours.invoke({
            "category": data.category,
            "description": data.description,
            "deliverables": data.deliverables,
        })

        if not result:
            raise HTTPException(status_code=400, detail="Failed to estimate hours")

        if isinstance(result, str):
            result_str = result.strip()
            if result_str.startswith("```json"):
                result_str = result_str.replace("```json", "").replace("```", "").strip()
            elif result_str.startswith("```"):
                result_str = result_str.replace("```", "").strip()

            try:
                result = json.loads(result_str)
            except json.JSONDecodeError as e:
                raise HTTPException(status_code=500, detail=f"Invalid JSON from LLM: {str(e)}")

        return response.EstimateHours(
            estimated_hours=float(result.get("estimated_hours", 0)),
            min_hours=float(result.get("min_hours", 0)),
            max_hours=float(result.get("max_hours", 0)),
            confidence=result.get("confidence", "UNKNOWN"),
            assumptions=result.get("assumptions", "")
        )

    except HTTPException:
        raise
    except KeyError as e:
        raise HTTPException(status_code=400, detail=f"Missing field in response: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Internal error: {str(e)}")


@app.post("/rank-bids", response_model=List[response.RankedBidResponse])
def rank_bids(data: request.RankBidsRequest):
    try:
        if not data.request_id or data.request_id <= 0:
            raise HTTPException(status_code=400, detail="Invalid request ID")

        if data.top_k and data.top_k <= 0:
            raise HTTPException(status_code=400, detail="top_k must be greater than 0")

        input_data = ingest.get_request_data(data.request_id)
        if not input_data:
            raise HTTPException(status_code=404, detail="Service request not found")

        bids = ingest.get_bids_for_request(data.request_id)
        if not bids:
            return []

        ranked = matcher.rank_bids_for_request(input_data, bids, top_k=data.top_k)

        if not ranked:
            return []

        output = []
        for bid, score in ranked:
            try:
                output.append(response.RankedBidResponse(
                    bid_id=bid['bid_id'],
                    company_name=bid['company_name'],
                    similarity_score=round(score, 3),
                    estimated_hours=bid['estimated_hours'],
                    token_amount=bid['token_amount'] if bid['token_amount'] > 0 else None,
                    payment_method=bid['payment_method'],
                    skills=bid['skills']
                ))
            except KeyError as e:
                raise HTTPException(status_code=400, detail=f"Missing field in bid data: {str(e)}")

        return output

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Internal error: {str(e)}")


@app.get("/health")
async def health_check():
    try:
        return {
            "status": "healthy",
            "pdf_db_loaded": model.pdf_exists,
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Health check failed: {str(e)}")