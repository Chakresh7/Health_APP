import logging
from uuid import uuid4

from fastapi import APIRouter, File, HTTPException, UploadFile, status

from app.schemas.food import FoodTextRequest, NutritionAnalysis
from app.services.food_analysis import analyze_image, analyze_text

router = APIRouter(prefix="/api/v1/food", tags=["food"])
logger = logging.getLogger(__name__)
ALLOWED_CONTENT_TYPES = {"image/jpeg", "image/png", "image/webp"}
MAX_IMAGE_BYTES = 10 * 1024 * 1024


def _map_analysis_error(error: Exception, request_id: str) -> HTTPException:
	if isinstance(error, RuntimeError):
		logger.error("Food analysis configuration failure request_id=%s", request_id)
		return HTTPException(status_code=503, detail="Cal.ai is temporarily unavailable.")
	logger.exception("Food analysis failed request_id=%s", request_id)
	return HTTPException(status_code=502, detail="We couldn't analyze this meal.")


@router.post("/analyze", response_model=NutritionAnalysis)
async def analyze_food(image: UploadFile = File(...)) -> NutritionAnalysis:
	request_id = str(uuid4())
	if image.content_type not in ALLOWED_CONTENT_TYPES:
		raise HTTPException(
			status_code=status.HTTP_415_UNSUPPORTED_MEDIA_TYPE,
			detail="Unsupported image type. Use JPEG, PNG, or WebP.",
		)

	image_bytes = await image.read(MAX_IMAGE_BYTES + 1)
	if not image_bytes:
		raise HTTPException(status_code=400, detail="Image is empty.")
	if len(image_bytes) > MAX_IMAGE_BYTES:
		raise HTTPException(status_code=413, detail="Image must be 10 MB or smaller.")

	try:
		return await analyze_image(image_bytes, image.content_type)
	except Exception as error:
		raise _map_analysis_error(error, request_id) from error


@router.post("/text", response_model=NutritionAnalysis)
async def analyze_food_from_text(payload: FoodTextRequest) -> NutritionAnalysis:
	request_id = str(uuid4())
	try:
		return await analyze_text(payload.text)
	except Exception as error:
		raise _map_analysis_error(error, request_id) from error
