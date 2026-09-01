import os
from functools import lru_cache
from pathlib import Path

from dotenv import load_dotenv

BACKEND_ROOT = Path(__file__).resolve().parents[2]
load_dotenv(BACKEND_ROOT / ".env")


class Settings:
	app_name: str = "Cal.ai API"
	environment: str = os.getenv("ENVIRONMENT", "development")
	log_level: str = os.getenv("LOG_LEVEL", "INFO")
	openai_api_key: str | None = os.getenv("OPENAI_API_KEY")
	openai_model: str = os.getenv("OPENAI_MODEL", "gpt-4o-mini")


@lru_cache
def get_settings() -> Settings:
	return Settings()
