from pydantic import BaseModel, Field, field_validator


class NutritionAnalysis(BaseModel):
	food_name: str = Field(min_length=1, max_length=200)
	portion_estimate: str = Field(min_length=1, max_length=200)
	estimated_calories: int = Field(ge=0, le=100_000)
	protein_g: float = Field(ge=0, le=10_000)
	carbs_g: float = Field(ge=0, le=10_000)
	fat_g: float = Field(ge=0, le=10_000)
	confidence: float = Field(ge=0, le=1)
	components: list[str] = Field(default_factory=list, max_length=50)

	@field_validator("components")
	@classmethod
	def validate_components(cls, components: list[str]) -> list[str]:
		return [component.strip() for component in components if component.strip()]


class FoodTextRequest(BaseModel):
	text: str = Field(min_length=1, max_length=2_000)
