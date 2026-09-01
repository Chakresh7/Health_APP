# 5. Validation and Roadmap

This document covers sections 42-47 of the Cal.ai specification. It defines post-MVP priorities, project risks, success measures, and the final product contract.

## 42. Refinement Roadmap

After the MVP is stable, prioritize:

### Phase 1: Better AI

Improve prompts, portion estimation, multiple-food detection, confidence handling, and uncertainty messaging.

### Phase 2: Better analytics

Add weekly calorie charts, macro trends, meal distribution, and a consistency score.

### Phase 3: Better personalization

Add user goals, custom calorie targets, macro targets, and personalized AI insights.

### Phase 4: Better UX

Add voice logging, favorites, recent foods, faster scanning, image thumbnails, and better animations.

## 43. Risks and Mitigations

### AI estimates are inaccurate

Label all values as estimates, allow manual editing, show confidence, and avoid medical claims.

### API latency

Use loading UI, image compression where needed, and clear progress states.

### API failure

Provide retry, manual entry fallback, and proper error messages.

### Scope creep

Freeze features after the end-to-end MVP works.

### API key exposure

Keep OpenAI credentials exclusively on the backend.

## 44. Success Metrics

### Functional

Food scanning works, AI returns structured nutrition, meals save correctly, dashboard totals are accurate, chat works, and data persists after restart.

### User experience

Food can be logged in under one minute, main actions are obvious, loading and error states are clear, and the app feels responsive.

### Technical

The project demonstrates clean Android architecture, API separation, Pydantic validation, Room persistence, no hardcoded API keys, and reasonable error handling.

### Presentation

The final project demonstrates Android, Kotlin, Jetpack Compose, camera integration, REST APIs, FastAPI, OpenAI multimodal AI, structured output, local persistence, and an AI-powered user experience.

## 45. Final Locked Stack

| Area | Technologies |
| --- | --- |
| Frontend | Android Studio, Kotlin, Jetpack Compose, Material 3, CameraX, Retrofit, Room, ViewModel, Coroutines |
| Backend | Python, FastAPI, Pydantic, Uvicorn, OpenAI Python SDK |
| AI | OpenAI multimodal model, vision, structured outputs, AI chat |
| Database | Room / SQLite |
| Development | Cursor, Android Studio, Git, GitHub, OpenAI API platform |

## 46. Final Product Scope

### Cal.ai v1

> An AI-powered Android nutrition tracker that lets users photograph or describe meals, receive estimated calorie and macronutrient information, save meals to a diary, monitor daily nutrition, and ask an AI nutrition assistant for contextual guidance.

- **Killer feature:** Food photo -> AI nutrition estimate -> one-tap diary entry.
- **Supporting feature:** Natural-language food logging and Ask Cal.ai.
- **Core utility:** Simple daily calorie and macro dashboard.

Everything else supports these three experiences.

## 47. Guiding Principle

> Build small. Make it work. Make it beautiful. Then make it smarter.

Do not sacrifice a stable core experience for extra features. The first milestone is the reliable flow:

```text
CAL.AI
  -> Scan Food
  -> Analyze
  -> Nutrition
  -> Add Meal
  -> Dashboard
  -> Ask Cal.ai
```

If this flow is fast, reliable, and polished, Cal.ai is already a strong AI Android project.
