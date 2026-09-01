# 3. Technical Architecture

This document covers sections 17-33 of the Cal.ai specification. It defines the implementation contracts used by the user flows in [User Experience](02-user-experience.md).

## 17. Frontend Technology

Use Android Studio, Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModel, StateFlow, CameraX, Retrofit, OkHttp, Room, Kotlin Coroutines, and Coil when image loading is needed.

Use a lightweight MVVM structure:

```text
UI -> ViewModel -> Repository -> Data Sources
                                  |-> Remote API
                                  |-> Local Room DB
```

## 18. Android Project Structure

```text
app/src/main/java/com/calai/
├── MainActivity.kt
├── navigation/AppNavigation.kt
├── data/local/{MealEntity.kt, MealDao.kt, AppDatabase.kt}
├── data/remote/{CalAiApi.kt, ApiModels.kt, ApiRepository.kt}
├── domain/model/Meal.kt
├── ui/home/{HomeScreen.kt, HomeViewModel.kt}
├── ui/scanner/{ScannerScreen.kt, ScannerViewModel.kt}
├── ui/result/{FoodResultScreen.kt, FoodResultViewModel.kt}
├── ui/chat/{ChatScreen.kt, ChatViewModel.kt}
├── ui/diary/{DiaryScreen.kt, DiaryViewModel.kt}
├── ui/components/
├── ui/theme/
└── utils/
```

## 19. Backend Technology

Use Python, FastAPI, Pydantic, the OpenAI Python SDK, Uvicorn, python-dotenv, and optionally Pillow for image handling.

The backend receives and validates requests, constructs prompts, calls OpenAI, enforces structured output, returns clean JSON, handles errors, and keeps credentials secret.

## 20. Backend Structure

```text
backend/
├── app/main.py
├── app/routes/{food.py, chat.py}
├── app/services/{openai_service.py, food_analysis.py, nutrition_service.py}
├── app/schemas/{food.py, chat.py}
├── app/core/config.py
├── .env
├── requirements.txt
└── README.md
```

## 21-23. Backend API

### `POST /api/v1/food/analyze`

Accept `multipart/form-data` with an `image` and return structured nutrition analysis, including food name, portion estimate, calories, protein, carbs, fat, confidence, and components.

### `POST /api/v1/food/text`

Accept JSON such as `{ "text": "I ate two rotis and paneer sabzi" }` and return structured estimated nutrition. The response must support the same review-before-save flow as image analysis.

### `POST /api/v1/chat`

Accept a message plus current daily calories, calorie target, macro totals, macro targets, and remaining context. Return `{ "reply": "..." }` with a contextual nutrition response.

## 24. OpenAI Integration

The vision path is:

```text
Android -> Image -> FastAPI -> OpenAI multimodal model
-> Structured output -> Pydantic validation -> Android
```

Prompts must identify visible foods, estimate portions and macros, return structured data, communicate uncertainty, and avoid medical certainty.

## 25. Structured AI Output

Use a Pydantic model equivalent to:

```python
class NutritionAnalysis(BaseModel):
    food_name: str
    portion_estimate: str
    estimated_calories: int
    protein_g: float
    carbs_g: float
    fat_g: float
    confidence: float
    components: list[str]
```

Validate model output before returning it to Android.

## 26. Prompt Design

The system prompt should describe Cal.ai as a nutrition tracking assistant, request approximate calorie and macronutrient estimates, state that visual estimates are not exact, require portion assumptions when uncertain, and return only requested structured fields. Refine the production prompt during testing.

## 27. Room Database

For MVP, store meals locally. A `MealEntity` contains `id`, `foodName`, `calories`, `proteinG`, `carbsG`, `fatG`, `mealType`, `timestamp`, and `imageUri`.

Required operations are `insertMeal()`, `getMealsForDay()`, `getMealsBetweenDates()`, `deleteMeal()`, `updateMeal()`, and `getDailyTotals()`.

## 28-29. Nutrition Calculations

Daily calories are the sum of breakfast, lunch, snack, and dinner calories. Remaining calories equal target minus consumed calories. When consumed calories exceed the target, show an explicit over-target status rather than an unexplained negative remaining value.

Protein, carbs, and fat are each calculated as the sum of the corresponding meal values. Progress is based on user targets.

## 30. UI Design Direction

The app should feel modern, minimal, AI-first, clean, friendly, fast, and not overly clinical. Prioritize large calorie numbers, clear hierarchy, rounded cards, a strong Scan CTA, simple bottom navigation, subtle AI branding, minimal text, useful empty states, and clear loading states.

Avoid too many charts or colors, dense nutrition tables, complex onboarding, and excessive animation.

## 31. Loading States

During AI requests show progress such as analyzing the meal, identifying food, estimating portion, and calculating nutrition. Never leave the user looking at a frozen screen.

## 32. Error Handling

Handle no internet, AI failure, denied camera permission, invalid images, and backend unavailability with clear messages and recovery actions. Include retry, retake, gallery, manual entry, or settings actions where appropriate.

## 33. Security

Never hardcode `OPENAI_API_KEY` in Android code. Keep the key in the backend environment, load it through configuration, and add `.env` to `.gitignore`.
