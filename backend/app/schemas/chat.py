from pydantic import BaseModel, Field


class ChatRequest(BaseModel):
	message: str = Field(min_length=1, max_length=2_000)
	calories_consumed: int = Field(default=0, ge=0, le=100_000)
	calorie_target: int = Field(default=2_000, ge=1, le=100_000)
	protein_g: float = Field(default=0, ge=0, le=10_000)
	carbs_g: float = Field(default=0, ge=0, le=10_000)
	fat_g: float = Field(default=0, ge=0, le=10_000)
	protein_target: float = Field(default=100, ge=0, le=10_000)
	carbs_target: float = Field(default=200, ge=0, le=10_000)
	fat_target: float = Field(default=65, ge=0, le=10_000)


class ChatResponse(BaseModel):
	reply: str = Field(min_length=1, max_length=4_000)
