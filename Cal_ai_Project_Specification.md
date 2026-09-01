# Cal.ai --- AI-Powered Calorie & Nutrition Tracker

## 1. Project Overview

**Project name:** Cal.ai\
**Platform:** Android\
**Primary development environment:** Android Studio\
**Frontend:** Kotlin + Jetpack Compose\
**Backend:** Python + FastAPI\
**AI:** OpenAI multimodal model via OpenAI API\
**Local database:** Room\
**Networking:** Retrofit / OkHttp\
**Development assistance:** Cursor\
**AI experimentation:** OpenAI API platform / Playground\
**Target MVP build time:** \~12 focused development hours\
**Rapid build window:** 6 days\
**Refinement window:** After MVP until final submission

### Product statement

> Cal.ai is an AI-powered nutrition companion that lets users photograph
> a meal or describe what they ate, estimates calories and
> macronutrients using AI, saves the meal to a personal food diary, and
> provides a simple daily nutrition dashboard and AI-generated insights.

### Primary goal

Build a small, polished Android application with one genuinely useful AI
capability rather than a large feature-heavy calorie tracker.

------------------------------------------------------------------------

# 2. Product Vision

Cal.ai should make food logging extremely low-friction.

Traditional calorie trackers require users to:

1.  Search for a food.
2.  Find the correct item.
3.  Select a serving.
4.  Enter quantity.
5.  Adjust nutrition values.
6.  Save the meal.

Cal.ai should reduce this to:

``` text
Take photo
    ↓
AI analyzes meal
    ↓
Review estimate
    ↓
Save
    ↓
Dashboard updates
```

The secondary interaction is:

``` text
"I ate 2 rotis, dal and curd"
    ↓
AI estimates nutrition
    ↓
Review
    ↓
Save
```

------------------------------------------------------------------------

# 3. Target Users

### Primary user

A student or young adult who wants a quick way to track meals and
understand approximate calorie/macronutrient intake.

### User characteristics

-   Uses Android smartphone.
-   Wants quick food logging.
-   Does not want to manually search large food databases.
-   Is comfortable using AI.
-   Wants a simple daily dashboard.
-   Understands that AI nutrition estimates are approximate.

------------------------------------------------------------------------

# 4. Core MVP Features

The MVP must contain the following features.

## 4.1 Home Dashboard

Display:

-   Today's calorie target.
-   Calories consumed.
-   Remaining calories.
-   Protein consumed / target.
-   Carbohydrates consumed / target.
-   Fat consumed / target.
-   Today's meals.
-   Primary "Scan Food" action.
-   AI daily insight.

### Example

``` text
Good morning 👋

Today's Calories

1,240 / 2,000 kcal
████████████░░░░

Protein
62 / 100 g

Carbs
140 / 200 g

Fat
42 / 65 g

Today's Meals

Breakfast       320 kcal
Lunch           620 kcal
Snack            80 kcal

[ Scan Food ]

✨ Cal.ai Insight
Your protein intake is currently
below your daily target.
```

------------------------------------------------------------------------

# 5. AI Food Scanner

This is the main feature of Cal.ai.

## User flow

``` text
Home
 ↓
Scan Food
 ↓
Camera / Gallery
 ↓
Select image
 ↓
Upload image
 ↓
AI analysis
 ↓
Nutrition result
 ↓
Edit if necessary
 ↓
Add to diary
```

## Camera requirements

Use Android CameraX.

Support:

-   Camera permission.
-   Rear camera.
-   Capture image.
-   Retake.
-   Gallery selection.
-   Image preview.
-   Loading state.
-   Error state.

------------------------------------------------------------------------

# 6. AI Food Analysis

The backend sends the image to the OpenAI API.

The model should identify:

-   Food / meal name.
-   Approximate portion.
-   Estimated calories.
-   Protein.
-   Carbohydrates.
-   Fat.
-   Confidence.
-   Optional detected food components.

### Example response

``` json
{
  "food_name": "Paneer Thali",
  "portion_estimate": "1 plate",
  "estimated_calories": 680,
  "protein_g": 28,
  "carbs_g": 82,
  "fat_g": 24,
  "confidence": 0.82,
  "components": [
    "paneer",
    "rice",
    "dal",
    "roti",
    "vegetables"
  ]
}
```

### Important product rule

All image-based nutrition values must be presented as **estimates**.

The application must not claim that visual calorie estimation is
medically or nutritionally exact.

------------------------------------------------------------------------

# 7. AI Result Screen

After analysis, show:

``` text
Food Detected

🍛 Paneer Thali

Estimated Nutrition

680 kcal

Protein       28 g
Carbs         82 g
Fat           24 g

Portion
1 plate

AI Confidence
82%

These values are AI estimates
and may vary based on portion size.

Meal Type

○ Breakfast
● Lunch
○ Snack
○ Dinner

[ Add to Diary ]
[ Retake ]
```

## Editing

The user should be able to edit:

-   Food name.
-   Calories.
-   Protein.
-   Carbs.
-   Fat.
-   Meal type.

This prevents the AI estimate from becoming an irreversible value.

------------------------------------------------------------------------

# 8. Natural Language Food Logging

Secondary AI feature.

User can enter:

> I ate 2 rotis, paneer sabzi and one glass of buttermilk.

The AI should return structured nutrition.

Example:

``` json
{
  "food_name": "2 Rotis + Paneer Sabzi + Buttermilk",
  "estimated_calories": 560,
  "protein_g": 22,
  "carbs_g": 70,
  "fat_g": 18,
  "portion_estimate": "2 rotis, 1 serving paneer sabzi, 1 glass buttermilk"
}
```

The user reviews the estimate before saving.

------------------------------------------------------------------------

# 9. Ask Cal.ai

A simple conversational interface.

Example questions:

-   "How many calories have I eaten today?"
-   "How much protein do I have left?"
-   "I have 600 calories left. What can I eat?"
-   "What was my highest-calorie meal today?"
-   "How am I doing today?"
-   "Give me a high-protein dinner under 500 calories."

## Context

The backend can provide the user's current daily totals to the AI.

Example:

``` text
Daily calories: 1400 / 2000
Protein: 65 / 100g
Carbs: 150 / 200g
Fat: 40 / 65g
Remaining: 600 kcal
```

The model can then generate a contextual response.

------------------------------------------------------------------------

# 10. Food Diary

Display saved meals.

## Today

``` text
Breakfast
Poha + Tea
320 kcal

Lunch
Paneer Thali
680 kcal

Snack
Apple
80 kcal

Total
1,080 kcal
```

Each meal should show:

-   Name.
-   Calories.
-   Macros.
-   Meal type.
-   Time.

Optional:

-   Food image thumbnail.
-   Delete action.
-   Edit action.

------------------------------------------------------------------------

# 11. History

Show previous daily totals.

Example:

``` text
Today
1,820 kcal

Yesterday
1,760 kcal

Aug 22
1,940 kcal

Aug 21
1,680 kcal
```

For MVP, history can remain simple.

Advanced charts are optional refinement work.

------------------------------------------------------------------------

# 12. Manual Meal Entry

Provide a fallback when the AI cannot correctly identify a meal.

Fields:

-   Food name.
-   Calories.
-   Protein.
-   Carbs.
-   Fat.
-   Meal type.

Example:

``` text
Add Food

Food name
[ Paneer Tikka ]

Calories
[ 280 ]

Protein
[ 18 ]

Carbs
[ 12 ]

Fat
[ 16 ]

Meal type
[ Dinner ]

[ Save Meal ]
```

------------------------------------------------------------------------

# 13. AI Daily Insight

Generate a short daily observation.

Example:

``` text
✨ Cal.ai Insight

You are at 70% of your calorie
target and 55% of your protein
target.

Your protein intake is relatively
low today.

Consider a protein-rich dinner.
```

The insight should be informational and not medical advice.

------------------------------------------------------------------------

# 14. User Goals

For MVP, use simple predefined targets.

Example:

``` text
Daily calorie target: 2,000 kcal
Protein target: 100 g
Carbs target: 200 g
Fat target: 65 g
```

Optional setup screen:

``` text
What's your goal?

○ Lose weight
○ Maintain weight
○ Gain weight
```

For the first version, goals can be manually configured.

Automatic calorie-target calculation is optional and should only be
added if time permits.

------------------------------------------------------------------------

# 15. Screens

## Required screens

### 1. Home

-   Daily calories.
-   Macros.
-   Meals.
-   Scan CTA.
-   AI insight.

### 2. Scan Food

-   Camera.
-   Gallery.
-   Preview.
-   Capture.

### 3. AI Analysis

-   Loading state.
-   Progress message.

### 4. Food Result

-   Food name.
-   Calories.
-   Macros.
-   Portion.
-   Confidence.
-   Edit.
-   Add to diary.

### 5. Ask Cal.ai

-   Chat messages.
-   Input field.
-   Send button.

### 6. Diary / History

-   Today's meals.
-   Previous days.
-   Totals.

### 7. Add Meal

-   Manual entry.

### 8. Settings

Minimal settings:

-   Daily calorie target.
-   Macro targets.
-   API/backend status if useful.
-   About Cal.ai.

------------------------------------------------------------------------

# 16. Navigation

Use bottom navigation.

``` text
Home       Scan       Ask Cal.ai       Diary
 🏠         📷           💬              📖
```

The Scan button can be visually emphasized.

------------------------------------------------------------------------

# 17. Frontend Technology

## Android

-   Android Studio.
-   Kotlin.
-   Jetpack Compose.
-   Material 3.
-   Navigation Compose.
-   ViewModel.
-   StateFlow.
-   CameraX.
-   Retrofit.
-   OkHttp.
-   Room.
-   Kotlin Coroutines.
-   Coil for image loading if needed.

## Suggested architecture

Use a lightweight MVVM structure.

``` text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Data Sources
 ├── Remote API
 └── Local Room DB
```

------------------------------------------------------------------------

# 18. Android Project Structure

``` text
cal-ai/
│
├── app/
│   └── src/main/java/com/calai/
│       │
│       ├── MainActivity.kt
│       │
│       ├── navigation/
│       │   └── AppNavigation.kt
│       │
│       ├── data/
│       │   ├── local/
│       │   │   ├── MealEntity.kt
│       │   │   ├── MealDao.kt
│       │   │   └── AppDatabase.kt
│       │   │
│       │   └── remote/
│       │       ├── CalAiApi.kt
│       │       ├── ApiModels.kt
│       │       └── ApiRepository.kt
│       │
│       ├── domain/
│       │   └── model/
│       │       └── Meal.kt
│       │
│       ├── ui/
│       │   ├── home/
│       │   │   ├── HomeScreen.kt
│       │   │   └── HomeViewModel.kt
│       │   │
│       │   ├── scanner/
│       │   │   ├── ScannerScreen.kt
│       │   │   └── ScannerViewModel.kt
│       │   │
│       │   ├── result/
│       │   │   ├── FoodResultScreen.kt
│       │   │   └── FoodResultViewModel.kt
│       │   │
│       │   ├── chat/
│       │   │   ├── ChatScreen.kt
│       │   │   └── ChatViewModel.kt
│       │   │
│       │   ├── diary/
│       │   │   ├── DiaryScreen.kt
│       │   │   └── DiaryViewModel.kt
│       │   │
│       │   ├── components/
│       │   └── theme/
│       │
│       └── utils/
│
├── build.gradle.kts
└── settings.gradle.kts
```

------------------------------------------------------------------------

# 19. Backend Technology

## Stack

-   Python.
-   FastAPI.
-   Pydantic.
-   OpenAI Python SDK.
-   Uvicorn.
-   python-dotenv.
-   Optional Pillow for image handling.

## Responsibilities

The backend should:

1.  Receive images.
2.  Validate requests.
3.  Construct AI prompts.
4.  Call OpenAI.
5.  Enforce structured output.
6.  Return clean JSON to Android.
7.  Handle errors.
8.  Keep API credentials secret.

------------------------------------------------------------------------

# 20. Backend Structure

``` text
backend/
│
├── app/
│   ├── main.py
│   │
│   ├── routes/
│   │   ├── food.py
│   │   └── chat.py
│   │
│   ├── services/
│   │   ├── openai_service.py
│   │   ├── food_analysis.py
│   │   └── nutrition_service.py
│   │
│   ├── schemas/
│   │   ├── food.py
│   │   └── chat.py
│   │
│   └── core/
│       └── config.py
│
├── .env
├── requirements.txt
└── README.md
```

------------------------------------------------------------------------

# 21. Backend API

## POST /api/v1/food/analyze

Purpose:

Analyze a food image.

Request:

``` text
multipart/form-data
image: <food image>
```

Response:

``` json
{
  "food_name": "Paneer Thali",
  "portion_estimate": "1 plate",
  "estimated_calories": 680,
  "protein_g": 28,
  "carbs_g": 82,
  "fat_g": 24,
  "confidence": 0.82,
  "components": [
    "paneer",
    "rice",
    "dal",
    "roti"
  ]
}
```

------------------------------------------------------------------------

# 22. POST /api/v1/food/text

Purpose:

Analyze a natural-language meal description.

Request:

``` json
{
  "text": "I ate two rotis and paneer sabzi"
}
```

Response:

``` json
{
  "food_name": "2 Rotis + Paneer Sabzi",
  "estimated_calories": 480,
  "protein_g": 20,
  "carbs_g": 60,
  "fat_g": 18
}
```

------------------------------------------------------------------------

# 23. POST /api/v1/chat

Purpose:

Ask Cal.ai a nutrition-related question.

Request:

``` json
{
  "message": "I have 600 calories left. What should I eat?",
  "daily_calories": 1400,
  "calorie_target": 2000,
  "protein_g": 65,
  "protein_target_g": 100,
  "carbs_g": 150,
  "carbs_target_g": 200,
  "fat_g": 40,
  "fat_target_g": 65
}
```

Response:

``` json
{
  "reply": "You have around 600 kcal remaining..."
}
```

------------------------------------------------------------------------

# 24. OpenAI Integration

Use the OpenAI API from the FastAPI backend.

### Vision workflow

``` text
Android
 ↓
Image
 ↓
FastAPI
 ↓
OpenAI multimodal model
 ↓
Structured output
 ↓
Pydantic validation
 ↓
Android
```

The model should be instructed to:

-   Identify visible foods.
-   Estimate portion size.
-   Estimate calories.
-   Estimate macros.
-   Return structured data.
-   Clearly treat values as estimates.
-   Avoid claiming medical certainty.

------------------------------------------------------------------------

# 25. Structured AI Output

Define a Pydantic model similar to:

``` python
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

The backend should validate the model response against this schema
before returning it to Android.

------------------------------------------------------------------------

# 26. Prompt Design

The AI system prompt should establish Cal.ai's behavior.

Conceptually:

``` text
You are Cal.ai, an AI nutrition tracking assistant.

Analyze food images and user meal descriptions.

Return approximate calorie and macronutrient estimates.

Never claim that visual estimates are exact.
When portion size is uncertain, state that the estimate
depends on portion assumptions.

Return only the requested structured fields.
```

The exact production prompt should be refined during testing.

------------------------------------------------------------------------

# 27. Room Database

For the MVP, store meals locally.

## MealEntity

``` text
id
foodName
calories
proteinG
carbsG
fatG
mealType
timestamp
imageUri
```

## Required operations

``` text
insertMeal()
getMealsForDay()
getMealsBetweenDates()
deleteMeal()
updateMeal()
getDailyTotals()
```

------------------------------------------------------------------------

# 28. Calorie Calculation

Daily calories:

``` text
dailyCalories =
    breakfastCalories
  + lunchCalories
  + snackCalories
  + dinnerCalories
```

Remaining calories:

``` text
remainingCalories =
    targetCalories - consumedCalories
```

Never display negative remaining calories without explicitly indicating
that the user has exceeded the target.

Example:

``` text
Target: 2,000 kcal
Consumed: 2,150 kcal

Status:
150 kcal over target
```

------------------------------------------------------------------------

# 29. Macro Calculation

For each macro:

``` text
consumedProtein =
    sum(meal.proteinG)

consumedCarbs =
    sum(meal.carbsG)

consumedFat =
    sum(meal.fatG)
```

Progress should be based on user targets.

------------------------------------------------------------------------

# 30. UI Design Direction

Cal.ai should feel:

-   Modern.
-   Minimal.
-   AI-first.
-   Clean.
-   Friendly.
-   Fast.
-   Not overly clinical.

### Design principles

-   Large calorie number.
-   Clear hierarchy.
-   Rounded cards.
-   Strong primary Scan CTA.
-   Simple bottom navigation.
-   Subtle AI branding.
-   Minimal text.
-   Good empty states.
-   Clear loading states.

Avoid:

-   Too many charts.
-   Too many colors.
-   Dense nutrition tables.
-   Complex onboarding.
-   Excessive animations.

------------------------------------------------------------------------

# 31. Loading States

AI requests can take time.

Show:

``` text
Analyzing your meal...

🔍 Identifying food
🥗 Estimating portion
📊 Calculating nutrition
```

Do not leave the user staring at a frozen screen.

------------------------------------------------------------------------

# 32. Error Handling

Handle:

### No internet

``` text
No internet connection.

Check your connection and try again.
```

### AI failure

``` text
We couldn't analyze this meal.

Try another photo with the food
clearly visible.
```

### Camera permission denied

``` text
Camera permission is required
to scan food.

[ Open Settings ]
```

### Invalid image

``` text
This doesn't look like a food image.

Try a clearer meal photo.
```

### Backend unavailable

``` text
Cal.ai is temporarily unavailable.

Please try again.
```

------------------------------------------------------------------------

# 33. Security

Never hardcode:

``` text
OPENAI_API_KEY
```

inside Android source code.

Use:

``` text
Android
   ↓
FastAPI
   ↓
Environment variable
   ↓
OpenAI
```

Backend `.env`:

``` text
OPENAI_API_KEY=...
```

Add `.env` to `.gitignore`.

------------------------------------------------------------------------

# 34. MVP Scope Boundary

## Must Have

-   Home dashboard.
-   Camera.
-   Gallery.
-   AI food analysis.
-   Nutrition result.
-   Editable result.
-   Save meal.
-   Room database.
-   Daily totals.
-   Diary.
-   Basic AI chat/text logging.
-   Error handling.
-   Polished UI.

## Should Have

-   AI daily insight.
-   Meal image thumbnails.
-   Simple history.
-   Manual meal entry.

## Could Have

-   Weekly charts.
-   Goal setup.
-   AI meal recommendations.
-   Voice input.
-   Food favorites.
-   Dark mode.
-   Cloud synchronization.

## Won't Have in MVP

-   Social network.
-   Friends.
-   Leaderboards.
-   Complex authentication.
-   Subscription system.
-   Payment system.
-   Large food database.
-   Wearable integrations.
-   Advanced medical recommendations.
-   Complex backend database.

------------------------------------------------------------------------

# 35. Six-Day Development Plan

## Day 1 --- Android Foundation

### Deliverables

-   Android Studio project.
-   Kotlin.
-   Compose.
-   Theme.
-   Navigation.
-   Home screen.
-   Scan screen.
-   Chat screen.
-   Diary screen.
-   Basic components.

### Success criteria

The entire app can be navigated even with dummy data.

------------------------------------------------------------------------

# Day 2 --- Local Nutrition Tracker

### Build

-   Room.
-   MealEntity.
-   MealDao.
-   Repository.
-   ViewModel.
-   Add meal.
-   Delete meal.
-   Daily totals.
-   Dashboard progress.

### Success criteria

The application works as a basic calorie tracker without AI.

------------------------------------------------------------------------

# Day 3 --- AI Vision

### Build

-   CameraX.
-   Gallery picker.
-   Image compression if needed.
-   Retrofit API.
-   FastAPI backend.
-   OpenAI integration.
-   Structured output.
-   Nutrition result screen.

### Success criteria

``` text
Photo
 ↓
OpenAI
 ↓
Nutrition
```

works end-to-end.

------------------------------------------------------------------------

# Day 4 --- Complete Cal.ai Flow

### Build

``` text
Scan
 ↓
Analyze
 ↓
Review
 ↓
Edit
 ↓
Save
 ↓
Dashboard
```

Also:

-   Natural-language meal logging.
-   AI chat.
-   Manual entry.

### Success criteria

Cal.ai feels like a complete product.

------------------------------------------------------------------------

# Day 5 --- UI/UX Refinement

Improve:

-   Dashboard.
-   Scan screen.
-   AI result.
-   Chat.
-   Diary.
-   Animations.
-   Empty states.
-   Loading states.
-   Error states.
-   Typography.
-   Spacing.
-   Icons.

### Success criteria

The application looks like a real product rather than a prototype.

------------------------------------------------------------------------

# Day 6 --- Testing + Submission

### Test

-   Camera permission.
-   Gallery.
-   Good food image.
-   Bad image.
-   AI failure.
-   No internet.
-   Room persistence.
-   App restart.
-   Meal deletion.
-   Meal editing.
-   Daily calculations.
-   Navigation.
-   Different Android screen sizes.

### Prepare

-   APK.
-   Source code.
-   README.
-   Screenshots.
-   Architecture diagram.
-   Demo video.
-   Project report.
-   Presentation.

------------------------------------------------------------------------

# 36. 12-Hour Initial Build Plan

The six-day schedule is a milestone schedule. The initial implementation
should target roughly 12 focused hours.

## Hour 0--1

Project setup.

## Hour 1--2

Navigation and basic screens.

## Hour 2--3

Dashboard UI.

## Hour 3--4

Room database.

## Hour 4--5

CameraX.

## Hour 5--6

FastAPI backend.

## Hour 6--7

OpenAI integration.

## Hour 7--8

AI result screen.

## Hour 8--9

Save meal and dashboard updates.

## Hour 9--10

AI text/chat.

## Hour 10--11

Diary + error handling.

## Hour 11--12

Polish + testing.

------------------------------------------------------------------------

# 37. Definition of Done --- MVP

Cal.ai MVP is complete when a tester can:

1.  Open the app.
2.  See today's calorie target.
3.  Tap Scan Food.
4.  Take a food photo.
5.  Send it to the backend.
6.  Receive an AI nutrition estimate.
7.  Review the result.
8.  Edit the estimate.
9.  Save the meal.
10. See the meal on the dashboard.
11. See updated daily calories.
12. Open the diary.
13. Ask Cal.ai a nutrition question.
14. Log a meal using natural language.
15. Restart the app and still see saved meals.

------------------------------------------------------------------------

# 38. Demo Scenario

The final demo should use one clean user journey.

### Demo

``` text
1. Open Cal.ai

2. Dashboard shows:
   1,240 / 2,000 kcal

3. Tap Scan Food

4. Photograph a meal

5. Cal.ai analyzes it

6. AI returns:
   Paneer Thali
   680 kcal
   28g protein
   82g carbs
   24g fat

7. User reviews the result

8. Tap Add to Diary

9. Dashboard changes:
   1,920 / 2,000 kcal

10. Ask Cal.ai:
    "What should I eat for dinner?"

11. Cal.ai responds based on
    remaining calories/macros.

12. Open Diary and show saved meal.
```

This is the primary presentation flow.

------------------------------------------------------------------------

# 39. Project Architecture Diagram

``` text
                         ┌─────────────────────┐
                         │       CAL.AI        │
                         │    Android App      │
                         │                     │
                         │ Kotlin              │
                         │ Jetpack Compose     │
                         │ CameraX             │
                         │ Retrofit            │
                         │ Room                │
                         └──────────┬──────────┘
                                    │
                                    │ HTTPS
                                    ▼
                         ┌─────────────────────┐
                         │       FastAPI       │
                         │      Backend        │
                         │                     │
                         │ API Routes          │
                         │ Validation          │
                         │ AI Service          │
                         │ Pydantic Schemas   │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │     OpenAI API      │
                         │                     │
                         │ Vision              │
                         │ Structured Output   │
                         │ AI Chat             │
                         └─────────────────────┘

                         ┌─────────────────────┐
                         │    Room Database    │
                         │                     │
                         │ Meals               │
                         │ Daily Totals        │
                         │ Meal History        │
                         └─────────────────────┘
```

------------------------------------------------------------------------

# 40. Development Workflow With Cursor

Do not ask Cursor to build the entire application in one prompt.

Use incremental prompts.

### Prompt sequence

``` text
1. Create Android project architecture.

2. Implement navigation.

3. Build HomeScreen.

4. Implement Room database.

5. Implement CameraX.

6. Create FastAPI backend.

7. Implement OpenAI service.

8. Connect Android to backend.

9. Build FoodResultScreen.

10. Connect save-meal flow.

11. Implement AI chat.

12. Refactor and test.
```

After every major feature:

``` text
Generate
 ↓
Run
 ↓
Test
 ↓
Fix
 ↓
Commit
```

------------------------------------------------------------------------

# 41. Git Strategy

Use small commits.

``` text
feat: initialize Android project
feat: add Compose navigation
feat: add home dashboard
feat: add Room database
feat: add CameraX scanner
feat: add FastAPI backend
feat: integrate OpenAI vision
feat: add nutrition result
feat: add meal diary
feat: add AI chat
fix: handle AI analysis errors
style: refine dashboard UI
```

------------------------------------------------------------------------

# 42. Refinement Roadmap

After the MVP is stable, prioritize improvements in this order.

## Phase 1 --- Better AI

-   Better food prompts.
-   Better portion estimation.
-   Multiple food detection.
-   Confidence handling.
-   Better uncertainty messaging.

## Phase 2 --- Better analytics

-   Weekly calorie chart.
-   Macro trends.
-   Meal distribution.
-   Consistency score.

## Phase 3 --- Better personalization

-   User goals.
-   Custom calorie targets.
-   Macro targets.
-   Personalized AI insights.

## Phase 4 --- Better UX

-   Voice logging.
-   Favorites.
-   Recent foods.
-   Faster scanning.
-   Image thumbnails.
-   Better animations.

------------------------------------------------------------------------

# 43. Risks

## Risk 1 --- AI nutrition estimates are inaccurate

Mitigation:

-   Label all values as estimates.
-   Allow manual editing.
-   Show confidence.
-   Avoid medical claims.

## Risk 2 --- API latency

Mitigation:

-   Loading UI.
-   Image compression.
-   Clear progress states.

## Risk 3 --- API failure

Mitigation:

-   Retry.
-   Manual entry fallback.
-   Proper error messages.

## Risk 4 --- Scope creep

Mitigation:

> Feature freeze after the MVP works end-to-end.

## Risk 5 --- API key exposure

Mitigation:

> Keep OpenAI credentials exclusively on the backend.

------------------------------------------------------------------------

# 44. Success Metrics for the Project

The project is successful if:

### Functional

-   Food scan works.
-   AI returns structured nutrition.
-   Meals save correctly.
-   Dashboard totals are accurate.
-   Chat works.
-   Data persists after app restart.

### UX

-   Food can be logged in under one minute.
-   Main actions are obvious.
-   Loading/error states are clear.
-   App feels responsive.

### Technical

-   Clean Android architecture.
-   Proper API separation.
-   Pydantic validation.
-   Room persistence.
-   No hardcoded API keys.
-   Reasonable error handling.

### Presentation

The project should demonstrate:

-   Android development.
-   Kotlin.
-   Jetpack Compose.
-   Camera integration.
-   REST APIs.
-   FastAPI.
-   OpenAI multimodal AI.
-   Structured AI output.
-   Local persistence.
-   AI-powered UX.

------------------------------------------------------------------------

# 45. Final Locked Stack

``` text
FRONTEND
──────────────
Android Studio
Kotlin
Jetpack Compose
Material 3
CameraX
Retrofit
Room
ViewModel
Coroutines


BACKEND
──────────────
Python
FastAPI
Pydantic
Uvicorn
OpenAI Python SDK


AI
──────────────
OpenAI multimodal model
Vision
Structured Outputs
AI Chat


DATABASE
──────────────
Room / SQLite


DEVELOPMENT
──────────────
Cursor
Android Studio
Git
GitHub
OpenAI API platform
```

------------------------------------------------------------------------

# 46. Final Product Scope

## Cal.ai v1

> **An AI-powered Android nutrition tracker that lets users photograph
> or describe meals, receive estimated calorie/macronutrient
> information, save meals to a diary, monitor daily nutrition, and ask
> an AI nutrition assistant for contextual guidance.**

### The killer feature

**📸 Food photo → AI nutrition estimate → one-tap diary entry**

### The supporting feature

**💬 Natural-language food logging + Ask Cal.ai**

### The core utility

**📊 Simple daily calorie and macro dashboard**

Everything else should support these three experiences.

------------------------------------------------------------------------

# 47. Guiding Principle

The project should follow one rule:

> **Build small. Make it work. Make it beautiful. Then make it
> smarter.**

Do not sacrifice a stable core experience for extra features.

The first milestone is not "build a huge AI nutrition platform."

It is:

``` text
                 CAL.AI

              📸 Scan Food
                    ↓
              🤖 Analyze
                    ↓
             🍛 Nutrition
                    ↓
              ➕ Add Meal
                    ↓
             📊 Dashboard
                    ↓
              💬 Ask Cal.ai
```

If this flow is fast, reliable, and polished, Cal.ai is already a strong
AI Android project.
