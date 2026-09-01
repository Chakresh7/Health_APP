from fastapi.testclient import TestClient

from app.main import app
from app.routes import food
from app.schemas.food import NutritionAnalysis


client = TestClient(app)


def test_rejects_unsupported_image_type() -> None:
    response = client.post(
        "/api/v1/food/analyze",
        files={"image": ("meal.txt", b"not an image", "text/plain")},
    )

    assert response.status_code == 415
    assert response.json()["detail"] == "Unsupported image type. Use JPEG, PNG, or WebP."


def test_analyzes_valid_image_without_calling_provider(monkeypatch) -> None:
    expected = NutritionAnalysis(
        food_name="Paneer Thali",
        portion_estimate="1 plate",
        estimated_calories=680,
        protein_g=28,
        carbs_g=82,
        fat_g=24,
        confidence=0.82,
        components=["paneer", "rice", "dal", "roti"],
    )

    async def fake_analysis(image_bytes: bytes, content_type: str) -> NutritionAnalysis:
        assert image_bytes == b"image-bytes"
        assert content_type == "image/jpeg"
        return expected

    monkeypatch.setattr(food, "analyze_image", fake_analysis)
    response = client.post(
        "/api/v1/food/analyze",
        files={"image": ("meal.jpg", b"image-bytes", "image/jpeg")},
    )

    assert response.status_code == 200
    assert response.json() == expected.model_dump()
