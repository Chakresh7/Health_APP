from fastapi import FastAPI

from app.routes.chat import router as chat_router
from app.routes.food import router as food_router

app = FastAPI(title="Cal.ai API", version="0.1.0")
app.include_router(food_router)
app.include_router(chat_router)


@app.get("/")
def root() -> dict[str, str]:
	return {
		"service": "Cal.ai API",
		"status": "ok",
		"health": "/health",
		"docs": "/docs",
	}


@app.get("/health")
def health_check() -> dict[str, str]:
	return {"status": "ok"}
