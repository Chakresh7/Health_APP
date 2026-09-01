# 2. User Experience

This document covers sections 5-16 of the Cal.ai specification. It defines the user-facing flows; implementation details belong in [Technical Architecture](03-technical-architecture.md).

## 5. AI Food Scanner

The main flow is:

```text
Home -> Scan Food -> Camera or Gallery -> Select image -> Upload image
-> AI analysis -> Nutrition result -> Edit if necessary -> Add to diary
```

Use Android CameraX. Support camera permission, rear-camera capture, retake, gallery selection, image preview, loading, and error states.

## 6. AI Food Analysis

The backend sends the image to OpenAI. The model identifies:

- Food or meal name.
- Approximate portion.
- Estimated calories.
- Protein, carbohydrates, and fat.
- Confidence.
- Optional detected food components.

Example response:

```json
{
  "food_name": "Paneer Thali",
  "portion_estimate": "1 plate",
  "estimated_calories": 680,
  "protein_g": 28,
  "carbs_g": 82,
  "fat_g": 24,
  "confidence": 0.82,
  "components": ["paneer", "rice", "dal", "roti", "vegetables"]
}
```

Every image-based value must be shown as an estimate. The app must not present visual calorie estimation as exact, medical, or nutritionally certain.

## 7. AI Result Screen

Show the detected food, estimated calories, protein, carbs, fat, portion, confidence, uncertainty message, meal type, **Add to Diary**, and **Retake** actions.

The user can edit food name, calories, protein, carbs, fat, and meal type before saving. AI output must never become irreversible.

## 8. Natural Language Food Logging

Users may enter a description such as:

> I ate 2 rotis, paneer sabzi and one glass of buttermilk.

Return structured nutrition, including food name, estimated calories, protein, carbs, fat, and portion estimate. The user reviews the result before saving.

## 9. Ask Cal.ai

Provide a simple conversational interface for questions such as:

- How many calories have I eaten today?
- How much protein do I have left?
- I have 600 calories left. What can I eat?
- What was my highest-calorie meal today?
- How am I doing today?
- Give me a high-protein dinner under 500 calories.

The backend may provide current daily calories, targets, macros, and remaining calories so responses are contextual.

## 10. Food Diary

Show today's saved meals with name, calories, macros, meal type, and time. Optional refinements are image thumbnails, delete, and edit actions.

Example entries include breakfast, lunch, and snack followed by a daily total.

## 11. History

Show simple previous daily totals, such as Today, Yesterday, and dated entries. Advanced charts are optional refinement work.

## 12. Manual Meal Entry

Provide a fallback form with food name, calories, protein, carbs, fat, and meal type, followed by **Save Meal**.

## 13. AI Daily Insight

Generate a short informational observation based on progress toward calorie and macro targets. It may suggest a protein-rich dinner, but it must clearly remain informational and not medical advice.

## 14. User Goals

For MVP, use configurable predefined targets such as:

- Daily calories: 2,000 kcal.
- Protein: 100 g.
- Carbs: 200 g.
- Fat: 65 g.

An optional setup can offer lose weight, maintain weight, or gain weight. Automatic calorie-target calculation is optional and only added if time permits.

## 15. Screens

Required screens:

1. **Home:** daily calories, macros, meals, Scan CTA, and AI insight.
2. **Scan Food:** camera, gallery, preview, and capture.
3. **AI Analysis:** loading state and progress message.
4. **Food Result:** nutrition, portion, confidence, editing, and save.
5. **Ask Cal.ai:** chat messages, input, and send action.
6. **Diary / History:** today's meals, previous days, and totals.
7. **Add Meal:** manual entry.
8. **Settings:** calorie target, macro targets, optional backend status, and About Cal.ai.

## 16. Navigation

Use bottom navigation with Home, Scan, Ask Cal.ai, and Diary. The Scan action may be visually emphasized because it is the primary product journey.
