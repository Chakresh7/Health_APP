# 4. MVP Delivery

This document covers sections 34-41 of the Cal.ai specification. It turns the product and architecture requirements into an execution sequence. Validation and post-MVP work are in [Validation and Roadmap](05-validation-roadmap.md).

## 34. MVP Scope Boundary

### Must have

- Home dashboard.
- Camera and gallery.
- AI food analysis.
- Editable nutrition result.
- Save meal.
- Room database and daily totals.
- Diary.
- Basic AI chat and text logging.
- Error handling.
- Polished UI.

### Should have

- AI daily insight.
- Meal image thumbnails.
- Simple history.
- Manual meal entry.

### Could have

- Weekly charts.
- Goal setup.
- AI meal recommendations.
- Voice input.
- Food favorites.
- Dark mode.
- Cloud synchronization.

### Excluded from MVP

Social networking, friends, leaderboards, complex authentication, subscriptions, payments, a large food database, wearable integrations, advanced medical recommendations, and a complex backend database.

## 35. Six-Day Development Plan

### Day 1: Android foundation

Create the Android project, Kotlin and Compose foundation, theme, navigation, Home, Scan, Chat, Diary, and basic components. The whole app should be navigable with dummy data.

### Day 2: Local nutrition tracker

Build Room, `MealEntity`, `MealDao`, repository, ViewModel, add/delete meal, daily totals, and dashboard progress. The app should work as a basic tracker without AI.

### Day 3: AI vision

Build CameraX, gallery selection, optional image compression, Retrofit, FastAPI, OpenAI integration, structured output, and the nutrition result screen. The photo-to-OpenAI-to-nutrition path should work end to end.

### Day 4: Complete Cal.ai flow

Connect scan, analysis, review, editing, save, and dashboard updates. Add natural-language logging, AI chat, and manual entry. The product should feel complete.

### Day 5: UI and UX refinement

Refine dashboard, scanner, result, chat, and diary screens, plus animations, empty states, loading, errors, typography, spacing, and icons. The app should look like a real product rather than a prototype.

### Day 6: Testing and submission

Test camera permission, gallery, valid and invalid images, AI failure, no internet, Room persistence, restart, deletion, editing, calculations, navigation, and different Android screen sizes. Prepare the APK, README, screenshots, architecture diagram, demo video, report, and presentation.

## 36. 12-Hour Initial Build Plan

- Hours 0-1: Project setup.
- Hours 1-2: Navigation and basic screens.
- Hours 2-3: Dashboard UI.
- Hours 3-4: Room database.
- Hours 4-5: CameraX.
- Hours 5-6: FastAPI backend.
- Hours 6-7: OpenAI integration.
- Hours 7-8: AI result screen.
- Hours 8-9: Save meal and dashboard updates.
- Hours 9-10: AI text and chat.
- Hours 10-11: Diary and error handling.
- Hours 11-12: Polish and testing.

## 37. Definition of Done: MVP

A tester can:

1. Open the app and see today's target.
2. Tap Scan Food and take a photo.
3. Send it to the backend and receive an AI estimate.
4. Review, edit, and save the result.
5. See the meal and updated daily calories on the dashboard.
6. Open the diary.
7. Ask Cal.ai a nutrition question.
8. Log a meal using natural language.
9. Restart the app and still see saved meals.

## 38. Demo Scenario

Use one clean journey: open Cal.ai with a dashboard around `1,240 / 2,000 kcal`, scan a meal, receive a Paneer Thali estimate of `680 kcal`, `28 g` protein, `82 g` carbs, and `24 g` fat, review and add it to the diary, verify the dashboard reaches `1,920 / 2,000 kcal`, ask what to eat for dinner, and open the diary.

This is the primary presentation flow.

## 39. Project Architecture Diagram

```text
Android App (Kotlin, Compose, CameraX, Retrofit, Room)
                    | HTTPS
                    v
FastAPI Backend (routes, validation, AI service, Pydantic)
                    |
                    v
OpenAI API (vision, structured outputs, chat)

Room / SQLite stores meals, daily totals, and history locally.
```

## 40. Development Workflow With Cursor

Use incremental prompts: create Android architecture, implement navigation, build HomeScreen, implement Room, implement CameraX, create FastAPI, implement OpenAI service, connect Android, build FoodResultScreen, connect saving, implement chat, then refactor and test.

After every major feature:

```text
Generate -> Run -> Test -> Fix -> Commit
```

## 41. Git Strategy

Use small commits such as `feat: initialize Android project`, `feat: add Compose navigation`, `feat: add home dashboard`, `feat: add Room database`, `feat: add CameraX scanner`, `feat: add FastAPI backend`, `feat: integrate OpenAI vision`, `feat: add nutrition result`, `feat: add meal diary`, `feat: add AI chat`, `fix: handle AI analysis errors`, and `style: refine dashboard UI`.
