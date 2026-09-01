import base64

from openai import AsyncOpenAI

from app.core.config import get_settings
from app.schemas.chat import ChatRequest
from app.schemas.food import NutritionAnalysis

VISION_SYSTEM_PROMPT = """You are Cal.ai, an AI nutrition tracking assistant.
Analyze the provided food image and return approximate calorie and
macronutrient estimates. Never claim that visual estimates are exact.
Base estimates on visible foods and reasonable portion assumptions.
Return only the requested structured fields."""

TEXT_SYSTEM_PROMPT = """You are Cal.ai, an AI nutrition tracking assistant.
Estimate calories and macronutrients from a meal description.
Never claim that estimates are exact. Assume typical restaurant or home
portions when the user omits quantities. Return only the requested structured fields."""

CHAT_SYSTEM_PROMPT = """You are Cal.ai, a nutrition tracking assistant for a mobile app.
Use the user's daily calorie and macro numbers when they are provided.
Keep replies to 2-4 short sentences. Be practical and friendly.
Never give medical advice, diagnoses, or certainty. All nutrition values are estimates."""


def _client() -> AsyncOpenAI:
	settings = get_settings()
	if not settings.openai_api_key:
		raise RuntimeError("OPENAI_API_KEY is not configured")
	return AsyncOpenAI(api_key=settings.openai_api_key)


async def analyze_food_image(image_bytes: bytes, content_type: str) -> NutritionAnalysis:
	client = _client()
	settings = get_settings()
	encoded_image = base64.b64encode(image_bytes).decode("ascii")
	response = await client.chat.completions.parse(
		model=settings.openai_model,
		messages=[
			{"role": "system", "content": VISION_SYSTEM_PROMPT},
			{
				"role": "user",
				"content": [
					{"type": "text", "text": "Analyze this meal as an estimate."},
					{
						"type": "image_url",
						"image_url": {
							"url": f"data:{content_type};base64,{encoded_image}"
						},
					},
				],
			},
		],
		response_format=NutritionAnalysis,
	)

	parsed = response.choices[0].message.parsed
	if parsed is None:
		raise ValueError("OpenAI returned no structured nutrition analysis")
	return parsed


async def analyze_food_text(description: str) -> NutritionAnalysis:
	client = _client()
	settings = get_settings()
	response = await client.chat.completions.parse(
		model=settings.openai_model,
		messages=[
			{"role": "system", "content": TEXT_SYSTEM_PROMPT},
			{
				"role": "user",
				"content": f"Estimate nutrition for this meal description:\n{description}",
			},
		],
		response_format=NutritionAnalysis,
	)

	parsed = response.choices[0].message.parsed
	if parsed is None:
		raise ValueError("OpenAI returned no structured nutrition analysis")
	return parsed


async def chat_reply(request: ChatRequest) -> str:
	client = _client()
	settings = get_settings()
	remaining = max(request.calorie_target - request.calories_consumed, 0)
	over = max(request.calories_consumed - request.calorie_target, 0)
	context = (
		f"Daily context (estimates):\n"
		f"- Calories: {request.calories_consumed} / {request.calorie_target} kcal "
		f"({'over by ' + str(over) + ' kcal' if over else str(remaining) + ' remaining'})\n"
		f"- Protein: {request.protein_g:.0f} / {request.protein_target:.0f} g\n"
		f"- Carbs: {request.carbs_g:.0f} / {request.carbs_target:.0f} g\n"
		f"- Fat: {request.fat_g:.0f} / {request.fat_target:.0f} g"
	)
	response = await client.chat.completions.create(
		model=settings.openai_model,
		messages=[
			{"role": "system", "content": CHAT_SYSTEM_PROMPT},
			{"role": "user", "content": f"{context}\n\nUser: {request.message}"},
		],
		max_tokens=220,
	)
	reply = (response.choices[0].message.content or "").strip()
	if not reply:
		raise ValueError("OpenAI returned an empty chat reply")
	return reply
