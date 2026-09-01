import logging
from uuid import uuid4

from fastapi import APIRouter, HTTPException

from app.schemas.chat import ChatRequest, ChatResponse
from app.services.openai_service import chat_reply

router = APIRouter(prefix="/api/v1", tags=["chat"])
logger = logging.getLogger(__name__)


@router.post("/chat", response_model=ChatResponse)
async def ask_cal_ai(payload: ChatRequest) -> ChatResponse:
	request_id = str(uuid4())
	try:
		return ChatResponse(reply=await chat_reply(payload))
	except RuntimeError as error:
		logger.error("Chat configuration failure request_id=%s", request_id)
		raise HTTPException(status_code=503, detail="Cal.ai is temporarily unavailable.") from error
	except Exception as error:
		logger.exception("Chat failed request_id=%s", request_id)
		raise HTTPException(status_code=502, detail="We couldn't answer that right now.") from error
