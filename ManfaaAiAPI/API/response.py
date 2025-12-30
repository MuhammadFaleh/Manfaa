from pydantic import BaseModel
from typing import Optional


class QueryResponse(BaseModel):
    answer: str


class EstimateHours(BaseModel):
    estimated_hours: float
    min_hours: float
    max_hours: float
    confidence: str
    assumptions: str


class RankedBidResponse(BaseModel):
    bid_id: int
    company_name: str
    skills: str
    similarity_score: float
    estimated_hours: float
    token_amount: Optional[float]
    payment_method: str