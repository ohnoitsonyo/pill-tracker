# Pill Tracker

A minimal Android home screen widget for tracking daily medication.

## Features

- 1×1 widget — tap to toggle between untaken (white) and taken (green)
- Resets automatically at midnight every day
- Missed resets caught on next boot with automatic backfill
- History screen showing taken/not-taken status for each past day

## Requirements

- Android 8.0+ (API 26)
- Tested on Samsung Galaxy S23 (Android 16 / API 36)

## Building

Requires Android Studio installed at `/Applications/Android Studio.app` (for the JDK).

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (device required)
./gradlew connectedAndroidTest

# Install to connected device
./gradlew installDebug
```

## Project structure

```
app/src/main/java/com/pilltracker/
├── data/          # Room database, DAO, repository
├── receiver/      # Widget provider, midnight reset, boot receiver
├── ui/            # History activity
└── util/          # AlarmScheduler, PillPrefs, ResetHelper
```

## Roadmap

- Notion sync for history
