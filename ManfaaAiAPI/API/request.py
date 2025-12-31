from typing import Optional

from pydantic import BaseModel


class QueryRequest(BaseModel):
    question: str


class EstimateHours(BaseModel):
    category: str
    description: str
    deliverables: str


class RankBidsRequest(BaseModel):
    request_id: int
    top_k: Optional[int] = 5

