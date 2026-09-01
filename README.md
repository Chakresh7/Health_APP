# Cal.ai

Cal.ai is an AI-powered Android nutrition tracker. Users can photograph or describe meals, review estimated nutrition, save meals locally, monitor daily totals, and ask an AI nutrition assistant for contextual guidance.

## Repository structure

- `app/`: Kotlin and Jetpack Compose Android application.
- `backend/`: FastAPI service and OpenAI integration boundary.
- `docs/`: Product specification and system architecture.
- `Cal_ai_Project_Specification.md`: Canonical full specification.

## Current scaffold

The Android app contains the initial Compose entry point, navigation shell, theme, and Home placeholder. The backend contains a FastAPI health endpoint and package boundaries for routes, services, schemas, and configuration.

## Planned implementation order

1. Android navigation and shared UI components.
2. Room meal storage and dashboard totals.
3. CameraX and gallery selection.
4. FastAPI nutrition endpoints and Pydantic schemas.
5. OpenAI analysis service.
6. Review, edit, and save flow.
7. Chat, diary, error handling, and observability.

See [System Architecture](docs/06-system-architecture.md) and [MVP Delivery](docs/04-mvp-delivery.md) for the architecture and delivery sequence.

## Backend local run

```text
python -m venv .venv
.venv\\Scripts\\Activate.ps1
pip install -r backend\\requirements.txt
cd backend
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

The health endpoint is available at `http://127.0.0.1:8000/health`.

### Phone testing

- Emulator: the app defaults to `http://10.0.2.2:8000/`.
- Physical phone on Wi-Fi: set **Settings → API base URL** to your PC LAN IP, for example `http://192.168.1.10:8000/`.
- USB: `adb reverse tcp:8000 tcp:8000`, then use `http://127.0.0.1:8000/` in Settings.
