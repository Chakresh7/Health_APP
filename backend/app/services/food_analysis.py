from app.schemas.food import NutritionAnalysis
from app.services.openai_service import analyze_food_image, analyze_food_text


async def analyze_image(image_bytes: bytes, content_type: str) -> NutritionAnalysis:
	return await analyze_food_image(image_bytes, content_type)


async def analyze_text(description: str) -> NutritionAnalysis:
	return await analyze_food_text(description)
