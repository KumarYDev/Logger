# 🪵 Kotlin Multiplatform (KMP) Cross-Platform Logging Architecture & Developer Guide

A production-ready architectural guide and reference implementation for **unified, cross-platform logging** in **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** targeting **Android**, **iOS**, **Desktop (macOS, Windows, Linux)**, and **Web (Wasm & JS)**.

This repository demonstrates how to architect a zero-overhead, multiplatform logging subsystem with **compile-time build variant control (Debug vs. Release)**, standardized function-scoped tagging, and platform-specific log inspection techniques.

An interactive, high-precision **Stopwatch & Countdown Timer** application is included as the living showcase to demonstrate how real-time MVI intents, monotonic timer tickers, state transitions, and clean architecture use cases are logged in practice.

---

## 📑 Table of Contents
- [🎯 The Problem: Logging in Kotlin Multiplatform](#-the-problem-logging-in-kotlin-multiplatform)
- [🏗️ Logging Subsystem Architecture](#️-logging-subsystem-architecture)
- [⚙️ Compile-Time Build Variant Control (Debug vs. Release)](#️-compile-time-build-variant-control-debug-vs-release)
- [🏷️ Standardized Tagging Convention](#️-standardized-tagging-convention)
- [🛠️ Developer Guide: How to Use the `Log` Facade](#️-developer-guide-how-to-use-the-log-facade)
- [📊 Platform-Specific Log Collection & Filtering](#-platform-specific-log-collection--filtering)
  - [🤖 1. Android (Logcat & adb)](#-1-android)
  - [🍏 2. iOS (Xcode, Console.app & log stream CLI)](#-2-ios)
  - [💻 3. Desktop (Stdout & grep)](#-3-desktop-macos-windows-linux)
  - [🌐 4. Web (Wasm & JS Browser DevTools)](#-4-web-wasm-js--javascript)
- [📱 Interactive Showcase App (Stopwatch & Timer)](#-interactive-showcase-app-stopwatch--timer)
- [📱 Screenshots & UI Showcase](#-screenshots--ui-showcase)
- [🏛️ Application Architecture & Project Structure](#️-application-architecture--project-structure)
- [🚀 How to Run Each Variant](#-how-to-run-each-variant)
- [🛠️ Verification & Compile Checks](#️-verification--compile-checks)

---

## 🎯 The Problem: Logging in Kotlin Multiplatform

In single-platform Android development, developers rely on `android.util.Log` and AGP's `BuildConfig.DEBUG`. However, in Kotlin Multiplatform (KMP):
1. **No Universal Standard Logging API:** `android.util.Log` is unavailable on iOS, Desktop, and Web.
2. **`BuildConfig.DEBUG` is Android-Only:** Standard AGP `BuildConfig` cannot be accessed from shared `commonMain` code without extra configuration.
3. **Release Build Leaks & Performance Overhead:** Leaving debug logging active in production releases leaks sensitive user telemetry and degrades performance through constant string concatenations and allocations.

### 💡 The Solution Implemented in this Architecture
* **Unified Facade:** A single [`Log`](file:///Volumes/Gate_Keeper/TrainingStudioProjects/Portfolio%20%20Projects/Logger%20Github/shared/src/commonMain/kotlin/com/asura/logger/Log.kt) object in `commonMain` wrapping [Touchlab Kermit](https://github.com/touchlab/Kermit).
* **Native Gradle Task for `BuildConfig`:** A configuration-cache compliant Gradle generator that creates `BuildConfig.isLogEnabled` directly in `commonMain`.
* **Zero Runtime Overhead When Disabled:** All logging functions gate execution with `if (!isEnabled) return`, bypassing string interpolation, formatting, and Kermit invocations entirely.
* **Universal Build Variant Gating:** Automatically disables logs on Release/Production tasks across Android, iOS, Desktop, and Web while allowing explicit CLI property overrides.

---

## 🏗️ Logging Subsystem Architecture

```
                                  ┌────────────────────────┐
                                  │   commonMain Source    │
                                  │ (ViewModels, UseCases) │
                                  └───────────┬────────────┘
                                              │ calls
                                              ▼
                                  ┌────────────────────────┐
                                  │      Log.kt Facade     │
                                  │ (Tagging + Gating Check)│
                                  └───────────┬────────────┘
                                              │
                      ┌───────────────────────┴───────────────────────┐
                      │ isEnabled == true                             │ isEnabled == false
                      ▼                                               ▼
         ┌────────────────────────┐                      ┌────────────────────────┐
         │ Touchlab Kermit Engine │                      │  Bypass / Early Return │
         └────────────┬───────────┘                      │(Zero String Allocation)│
                      │                                  └────────────────────────┘
     ┌────────────────┼────────────────┬────────────────┐
     ▼                ▼                ▼                ▼
┌──────────┐    ┌──────────┐     ┌───────────┐    ┌───────────┐
│ Android  │    │   iOS    │     │  Desktop  │    │ Web (Wasm/│
│  Logcat  │    │NSLog/OSLog│    │  Stdout   │    │JS Console)│
└──────────┘    └──────────┘     └───────────┘    └───────────┘
```

---

## ⚙️ Compile-Time Build Variant Control (Debug vs. Release)

Rather than relying on brittle third-party plugins, this repository uses a **Native Gradle Task** defined in [`shared/build.gradle.kts`](file:///Volumes/Gate_Keeper/TrainingStudioProjects/Portfolio%20%20Projects/Logger%20Github/shared/build.gradle.kts).

### How It Works:
1. **Automatic Variant Detection:** Gradle inspects task names to check if a release/distribution task is running (e.g., `assembleRelease`, `linkReleaseFramework`, `wasmJsBrowserDistribution`).
2. **Code Generation:** The task generates `BuildConfig.kt` into `commonMain`:
   ```kotlin
   package com.asura.logger

   object BuildConfig {
       const val isLogEnabled: Boolean = true // false on release builds
   }
   ```
3. **CLI Override Support:** You can explicitly enable or disable logging during any build via Gradle flags:
   ```bash
   # Force logs disabled in debug:
   ./gradlew assembleDebug -PisLogEnabled=false

   # Force logs enabled in release:
   ./gradlew assembleRelease -PisLogEnabled=true
   ```

---

## 🏷️ Standardized Tagging Convention

All logs adhere to a strict function-scoped tagging standard:
$$\text{Tag} = \texttt{ClassName\_functionName()}$$

* **Global Base Tag:** `Log.APP_TAG` (`"ASURA_LOGGER"`).
* **Resolved Tag Format:** `ASURA_LOGGER_ClassName_functionName()`.
* **Benefit:** Allows developers to filter logs either globally (`ASURA_LOGGER`) or pinpoint a specific ViewModel, UseCase, or Repository (`TimerViewModel_` or `StopwatchRepositoryImpl_`).

### 📑 Log Levels
* **`Log.v(message, tag)` (Verbose):** Ultra high-frequency events (e.g. tick intervals, micro-calculations).
* **`Log.d(message, tag)` (Debug):** Granular execution traces, incoming MVI intents, user gestures, and wheel scroll snaps.
* **`Log.i(message, tag)` (Info):** State transitions, lifecycle changes, and use-case outcomes.
* **`Log.w(message, tag)` (Warning):** Recoverable anomalies or unexpected states.
* **`Log.e(message, tag, throwable)` (Error):** Handled exceptions and critical operational failures.
* **`Log.wtf(message, tag, throwable)` (Assert):** Critical invariant violations that should never happen.

---

## 🛠️ Developer Guide: How to Use the `Log` Facade

Invoke `Log` methods anywhere in `commonMain` or platform modules:

```kotlin
class TimerViewModel(
    private val calculateProgressUseCase: CalculateTimerProgressUseCase
) : MviViewModel<TimerState, TimerIntent, TimerEffect>(TimerState()) {

    // 1. Log incoming MVI intent
    override fun handleIntent(intent: TimerIntent) {
        Log.d("Handling intent: $intent", tag = "TimerViewModel_handleIntent()")
        when (intent) {
            is TimerIntent.Start -> startTimer()
            is TimerIntent.Pause -> pauseTimer()
            is TimerIntent.Reset -> resetTimer()
        }
    }

    // 2. Log state transitions and side-effects
    private fun startTimer() {
        Log.i("Starting countdown: ${currentState.duration}", tag = "TimerViewModel_startTimer()")
        setState { copy(isRunning = true) }
        sendEffect(TimerEffect.Started)
    }

    // 3. Log errors with exceptions
    private fun handleError(throwable: Throwable) {
        Log.e("Timer calculation failed", tag = "TimerViewModel_handleError()", throwable = throwable)
    }
}
```

---

## 📊 Platform-Specific Log Collection & Filtering

### 🤖 1. Android
* **Android Studio Logcat:**
  Filter query:
  ```text
  tag:ASURA_LOGGER OR tag:TimerViewModel_ OR tag:StopwatchViewModel_
  ```
* **Terminal (`adb logcat`):**
  ```bash
  adb logcat -s "ASURA_LOGGER:*" "ASURA_LOGGER_TimerViewModel_handleIntent():*"
  ```

---

### 🍏 2. iOS
* **Xcode Debug Console:**
  Run the app in Xcode (`⌘ + R`). Logs stream live to the bottom Debug Area console.
* **macOS `Console.app` (Live GUI for connected iPhone / Simulator):**
  1. Open `/System/Applications/Utilities/Console.app` and select your iPhone under **Devices**.
  2. Click **Action** in the menu bar and enable **"Include Info Messages"** and **"Include Debug Messages"**.
  3. Filter by `process:iosApp` and search for `ASURA_LOGGER`.
* **macOS Terminal (`log stream` CLI):**
  ```bash
  log stream --device "<DEVICE_NAME_OR_UDID>" --process "iosApp" --level debug --predicate 'eventMessage CONTAINS "ASURA_LOGGER"'
  ```

---

### 💻 3. Desktop (macOS, Windows, Linux)
Desktop targets print logs directly to standard output:
```bash
./gradlew :desktopApp:run
```
Filter output live in terminal:
```bash
./gradlew :desktopApp:run | grep -E "ASURA_LOGGER|TimerViewModel|StopwatchViewModel"
```

---

### 🌐 4. Web (Wasm-JS & JavaScript)
1. Run the Web application:
   ```bash
   ./gradlew :webApp:wasmJsBrowserDevelopmentRun
   ```
2. Open Browser Developer Tools (**F12** or **Cmd + Option + I**) -> **Console** tab.
3. Filter console output by typing `ASURA_LOGGER` or specific class names.

---

## 📱 Interactive Showcase App (Stopwatch & Timer)

To validate the logging architecture under real-world conditions (high-frequency monotonic ticks, background coroutines, configuration changes, and MVI state cycles), the repository includes a complete **Stopwatch & Countdown Timer** reference app:

- **Monotonic Precision:** Uses Kotlin's monotonic `TimeMark` provider for drift-free timing.
- **MVI Architecture:** Unidirectional data flow (`UiState`, `UiIntent`, `UiEffect`) where every intent and effect is traced.
- **Adaptive Layout:** Responsive single-column phone layout and 2-column dual-pane layout for landscape mode, foldables, and desktop/web viewports.

---

## 📱 Screenshots & UI Showcase

### 📱 Mobile Portrait Modes

| ⏱️ Stopwatch Mode | ⏳ Timer Picker Mode | ⏳ Timer Countdown Mode |
| :---: | :---: | :---: |
| <img src="./docs/screenshots/stopwatch_portrait.png" width="270" alt="Stopwatch Portrait" /> | <img src="./docs/screenshots/timer_wheel_picker.png" width="270" alt="Timer Wheel Picker" /> | <img src="./docs/screenshots/timer_running_ring.png" width="270" alt="Timer Running Ring" /> |
| *Monotonic stopwatch & lap splits* | *Snap wheel picker & presets* | *Animated circular progress ring* |

---

### 📐 Adaptive & Dual-Pane Mode

| 📱 Phone Landscape (Dual-Pane) | 📱 Foldable / Tablet (Unfolded) |
| :---: | :---: |
| <img src="./docs/screenshots/stopwatch_landscape.png" width="450" alt="Stopwatch Landscape" /> | <img src="./docs/screenshots/foldable_unfolded.png" width="450" alt="Foldable Unfolded" /> |
| *Side-by-side: Clock & controls on left, Lap table on right* | *Optimized for wide unfolded foldable screens and tablets* |

---

### 🧭 Floating Navigation Bar

<p align="center">
  <img src="./docs/screenshots/floating_nav_bar.png" width="340" alt="Floating Navigation Bar" />
  <br />
  <em>Logo-only floating navigation bar with spring-animated selection indicator</em>
</p>

---

## 🏛️ Application Architecture & Project Structure

The project strictly follows **Clean Architecture** and **MVI**:

```
Logger/
├── androidApp/                     # Android application module
├── iosApp/                         # iOS application module (SwiftUI wrapper)
├── desktopApp/                     # Desktop JVM application module (macOS, Windows, Linux)
├── webApp/                         # Web application module (Wasm-JS & JS)
└── shared/                         # Core multiplatform shared module
    └── src/commonMain/kotlin/com/asura/logger/
        ├── Log.kt                  # Centralized cross-platform logging facade
        ├── core/                   # Shared architectural foundations
        │   ├── mvi/                # MviViewModel, UiState, UiIntent, UiEffect
        │   └── util/               # TimeFormatter
        ├── domain/                 # Pure domain logic (Models, UseCases, Repositories)
        │   ├── model/              # Lap, StopwatchRecord, TimerDuration
        │   ├── repository/         # StopwatchRepository, TimerRepository
        │   └── usecase/            # CalculateLapSplits, NormalizeDuration, CalculateTimerProgress
        ├── data/                   # Data sources and repository implementations
        │   ├── datasource/         # MonotonicTimeSourceProvider
        │   └── repository/         # StopwatchRepositoryImpl, TimerRepositoryImpl
        └── presentation/           # UI layer (Compose Multiplatform & MVI)
            ├── navigation/         # FloatingNavBar, AppScreen
            ├── stopwatch/          # StopwatchScreen, StopwatchViewModel, StopwatchContract
            └── timer/              # TimerScreen, TimerViewModel, TimerContract
```

---

## 🚀 How to Run Each Variant

### Prerequisites
- **JDK 17 or higher** (Android Studio JBR or OpenJDK 17+)
- **Android Studio** (Ladybug / Meerkat or newer)
- **Xcode** (15+ for iOS compilation on macOS)

---

### 🤖 1. Android
```bash
# Debug install
./gradlew :androidApp:installDebug

# Release build (logs automatically disabled)
./gradlew :androidApp:assembleRelease
```

---

### 🍏 2. iOS
1. Open `iosApp/iosApp.xcodeproj` in **Xcode**.
2. Select your target simulator (e.g., iPhone 15 Pro) or physical device.
3. Press **Run (⌘ + R)**.

---

### 💻 3. Desktop (macOS, Windows, Linux)
```bash
# Run JVM desktop app
./gradlew :desktopApp:run
```

---

### 🌐 4. Web Application (Wasm & JS)
```bash
# Wasm-JS target (Recommended)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# JavaScript target
./gradlew :webApp:jsBrowserDevelopmentRun
```

---

## 🛠️ Verification & Compile Checks

To verify all targets and metadata compile cleanly from the command line:

```bash
./gradlew generateBuildConfig compileCommonMainKotlinMetadata compileKotlinJvm compileKotlinJs :androidApp:assembleDebug
```