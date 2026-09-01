# Cal.ai Documentation

This directory contains the Cal.ai project specification divided into focused documents. The original [Cal.ai Project Specification](../Cal_ai_Project_Specification.md) remains the canonical full reference.

## Document map

| Document | Sections | Purpose |
| --- | --- | --- |
| [01 Product Foundation](01-product-foundation.md) | 1-4 | Product identity, vision, users, and MVP foundation |
| [02 User Experience](02-user-experience.md) | 5-16 | Food scanning, AI interactions, diary, screens, and navigation |
| [03 Technical Architecture](03-technical-architecture.md) | 17-33 | Android, backend, APIs, AI integration, persistence, UI states, and security |
| [04 MVP Delivery](04-mvp-delivery.md) | 34-41 | Scope boundaries, six-day plan, 12-hour plan, definition of done, demo, and workflow |
| [05 Validation and Roadmap](05-validation-roadmap.md) | 42-47 | Refinement priorities, risks, success metrics, locked stack, final scope, and principle |
| [06 System Architecture](06-system-architecture.md) | Architecture extension | Scalability, observability, monitoring, logging, security, resilience, and deployment |

## Consistency rules

- Cal.ai is an Android application built with Kotlin and Jetpack Compose.
- AI nutrition values are estimates and must remain editable by the user.
- OpenAI credentials stay on the FastAPI backend and never ship in Android code.
- Room is the local source of truth for saved meals and daily totals.
- The primary journey is: scan or describe food, analyze, review, edit if needed, save, and update the dashboard.
- MVP priorities take precedence over optional refinement work.
- The MVP is local-first with a stateless FastAPI AI gateway; production infrastructure is added only as scale and product needs justify it.

When changing a requirement, update the owning document and the original specification so both references retain the same product meaning.
