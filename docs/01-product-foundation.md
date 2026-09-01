# 1. Product Foundation

This document covers sections 1-4 of the Cal.ai specification. See [User Experience](02-user-experience.md) for feature flows and [Technical Architecture](03-technical-architecture.md) for implementation boundaries.

## 1. Project Overview

**Project name:** Cal.ai  
**Platform:** Android  
**Primary development environment:** Android Studio  
**Frontend:** Kotlin + Jetpack Compose  
**Backend:** Python + FastAPI  
**AI:** OpenAI multimodal model via OpenAI API  
**Local database:** Room  
**Networking:** Retrofit / OkHttp  
**Development assistance:** Cursor  
**AI experimentation:** OpenAI API platform / Playground  
**Target MVP build time:** Approximately 12 focused development hours  
**Rapid build window:** 6 days  
**Refinement window:** After MVP until final submission

### Product statement

> Cal.ai is an AI-powered nutrition companion that lets users photograph a meal or describe what they ate, estimates calories and macronutrients using AI, saves the meal to a personal food diary, and provides a simple daily nutrition dashboard and AI-generated insights.

### Primary goal

Build a small, polished Android application with one genuinely useful AI capability rather than a large feature-heavy calorie tracker.

## 2. Product Vision

Cal.ai should make food logging extremely low-friction. Traditional trackers require food search, item selection, serving and quantity entry, nutrition adjustment, and saving. Cal.ai reduces this to:

```text
Take photo -> AI analyzes meal -> Review estimate -> Save -> Dashboard updates
```

The secondary interaction is:

```text
Describe what was eaten -> AI estimates nutrition -> Review -> Save
```

## 3. Target Users

The primary user is a student or young adult who wants to quickly track meals and understand approximate calorie and macronutrient intake.

Users generally:

- Use an Android smartphone.
- Want quick food logging.
- Do not want to search a large food database.
- Are comfortable using AI.
- Want a simple daily dashboard.
- Understand that AI nutrition estimates are approximate.

## 4. Core MVP Features

The MVP must include:

- Today's calorie target, calories consumed, and remaining calories.
- Protein, carbohydrate, and fat consumed against their targets.
- Today's meals.
- A primary **Scan Food** action.
- An AI daily insight.
- AI food scanning through camera or gallery.
- Editable AI nutrition results before saving.
- Natural-language food logging.
- A food diary and simple history.
- Manual meal entry as a fallback.
- Ask Cal.ai conversational support.
- Local persistence with Room.
- Clear loading and error states.

The central product promise is a fast path from food input to a reviewed, editable diary entry. All nutrition estimates remain approximate and informational, never medical certainty.
