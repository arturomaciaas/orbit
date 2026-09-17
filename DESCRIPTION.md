# Orbit — Personal App Blocker

**Platform:** Native Android (Kotlin + Jetpack Compose), built for a Samsung Galaxy S24+ on Android 14/15. Fully local, single-user, no accounts or cloud.

**Concept:** A distraction blocker with a twist. When you try to open a blocked app, Orbit intercepts it and makes you answer quiz questions before you get in. Staying focused grows an animated cosmos; slipping up triggers a meteor strike that sets you back. Notifications from noisy apps get filtered into tiers so only what matters reaches you.

## Core features

**App blocking (hard interception)**
- Pick any installed app and block it in one of two modes: a fixed **duration** (X minutes/hours/days) or during **focus sessions**.
- An accessibility service watches for the blocked app coming to the foreground and throws a full-screen quiz gate in front of it.
- Answer the required number of questions correctly and you get a **5-minute access window**, after which it re-gates. The number of questions required is configurable.

**Quiz gate**
- Questions come from a local, hand-editable bank across four topics: **System Design, Chess, Software Engineering, and AWS**.
- Ships with a curated starter set (~24 questions) that you can add to, edit, or delete.
- Questions are drawn as a balanced mix across topics. A wrong answer feeds the failure/setback system.

**Focus timer sessions**
- Start a timed focus session (15/25/45/60/90 min) over a chosen set of apps.
- A foreground-service notification shows the live countdown.
- Completing a session grows your cosmos; ending early counts as aborted (no growth).

**Space-themed gamification**
- Your progress is a living cosmos that evolves through five stages: **Planet -> Moon -> Rings -> System -> Galaxy**.
- Each completed focus session adds growth; crossing a threshold advances the stage.
- A **wrong quiz answer triggers a "meteor strike"** — a partial setback that can knock progress down (but never wipes everything).
- Tracks total sessions, current streak, and longest streak (streaks based on consecutive-day activity).

**Tiered notification filtering**
- A notification listener sorts each app's notifications into three tiers you assign per app:
  - **Priority** — passes through untouched, with sound (e.g. Phone, WhatsApp calls).
  - **Peek** — the original is canceled and replaced with a trimmed, silent notification showing just the sender + a short preview, so you can decide if it's worth engaging.
  - **Suppress** — hidden entirely and collected in an in-app digest.
- Calls and ongoing/system notifications are never interfered with.
- A **Digest** screen collects filtered notifications with read/clear controls.

**Backup & restore**
- Export your blocks, question bank, cosmos progress, and focus history to a **JSON file** (via the system file picker), and import it back to restore.
- Versioned, tolerant format so backups survive minor updates.

**Onboarding & permissions**
- First-run flow guides you through granting Accessibility and Notification access (and notification-display permission on Android 13+), with live status checks.
- Permissions are re-checked and re-promptable from Settings; you can re-run the setup guide anytime.

## The look
- Dark, "liquid glass" (glassmorphism) UI: frosted translucent cards and pills with rounded corners, over a **living animated space background** — parallax twinkling starfields, drifting nebula clouds, and periodic shooting stars.
- The Home screen centerpiece is an animated planet/galaxy: a spinning shaded planet with a glowing atmosphere, tilted rings, orbiting moons with motion trails, and spiral arms at the galaxy stage. A meteor animation plays on setbacks.

## App structure (six tabs)
1. **Cosmos** — the animated home screen with your galaxy, growth %, and stats.
2. **Focus** — start/monitor focus sessions.
3. **Blocks** — manage blocked apps, block rules, and per-app notification tiers.
4. **Quiz Bank** — create and manage quiz questions.
5. **Digest** — review suppressed/peek notifications.
6. **Settings** — permissions, quiz difficulty, backup/restore, re-run setup.

## Under the hood
- Kotlin + Jetpack Compose (Material 3), Room (local database), DataStore (settings), WorkManager (expired-block cleanup), Hilt (dependency injection).
- Clean layered architecture (data / domain / service / ui). Core logic — block rules, quiz grading, focus timing, gamification math, notification classification, backup round-trip — is covered by unit tests.
- Blocking uses an AccessibilityService; filtering uses a NotificationListenerService. No root, no device-admin lockdown; bypass resistance is "moderate" by design.
