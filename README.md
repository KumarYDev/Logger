# ⏱️ Multiplatform Stopwatch & Timer (KMP + Compose Multiplatform)

A Kotlin Multiplatform (KMP) application providing a high-precision **Stopwatch** and **Countdown Timer** with an adaptive UI targeting **Android**, **iOS**, **Desktop (macOS, Windows, Linux)**, and **Web (Wasm & JS)**.

Built following **Clean Architecture** (Domain, Data, Presentation, Core) and the **MVI (Model-View-Intent)** architectural pattern.

---

## 📱 Screenshots

### ⏱️ Stopwatch (Portrait)

![Stopwatch Portrait](./docs/screenshots/stopwatch_portrait.png)

*Drift-free monotonic stopwatch with lap splits and smooth controls.*

---

### ⏳ Timer Duration Wheel Picker (Configuring Mode)

![Timer Wheel Picker](./docs/screenshots/timer_wheel_picker.png)

*Scrollable snap wheel picker for Hours, Minutes, and Seconds with quick preset chips.*

---

### ⏳ Timer Countdown (Running / Active Mode)

![Timer Running Ring](./docs/screenshots/timer_running_ring.png)

*Animated circular countdown progress gauge with real-time remaining countdown.*

---

### 📐 Dual-Pane Mode (Phone Landscape & Foldable Unfolded Screens)

![Stopwatch Landscape](./docs/screenshots/stopwatch_landscape.png)

*Side-by-side dual-pane layout: Clock & Controls on left, full-height Lap Table on right.*

![Foldable Unfolded](./docs/screenshots/foldable_unfolded.png)

*Dual-pane layout optimized for wide unfolded foldable screens and tablets.*

---

### 🧭 Floating Navigation Bar

![Floating Navigation Bar](./docs/screenshots/floating_nav_bar.png)

*Logo-only floating navigation bar with spring-animated selection indicator.*

---

## ✨ Features

### ⏱️ Stopwatch
- **Drift-Free Precision**: Powered by Kotlin's monotonic `TimeMark` provider for sub-second accuracy without system time drift.
- **Lap Splitting**: Records individual lap durations alongside total cumulative elapsed time.
- **Auto-Scrolling Lap List**: Automatically scrolls to reveal the latest recorded lap at the top of the table.
- **Uniform Styling**: Clean row dividers and structured columns (`Lap #`, `Lap Time`, `Total Time`).
- **Smooth Spring Controls**: Smooth, spring-animated button state transitions for `Start`, `Pause`, `Resume`, `Lap`, and `Reset`.

### ⏳ Countdown Timer
- **Interactive Snap Wheel Picker**: 3-column scrollable vertical wheel picker (`Hours`, `Minutes`, `Seconds`) with snapping physics and isolated programmatic synchronization.
- **Animated Countdown Gauge**: Smooth crossfade from configuration wheels to an animated circular progress ring with real-time remaining countdown display.
- **Quick Preset Chips**: Fast one-tap presets (`+30s`, `+1m`, `+5m`, `+10m`) for initial duration setup and runtime time extensions.
- **Completion Alert**: Animated banner notifying the user upon countdown completion with a dismiss action.

### 📐 Adaptive Layout & Rotation Support
- **Responsive Viewport Reflow**:
  - **Phone Portrait & Folded Outer Screens**: Single-column vertical layout with dedicated clearance for floating navigation.
  - **Unfolded Foldables, Tablets & Phone Landscape**: Dynamic **2-Column Dual-Pane** layout placing the Clock/Wheel on the left and Controls/Laps on the right.
- **Rotation & Lifecycle Continuity**:
  - `rememberSaveable` preserves active tab state across Activity recreation and configuration changes.
  - Tickers and monotonic coroutines continue counting seamlessly during rotation, folding, or unfolding.
- **Floating Logo-Only Navigation Bar**:
  - Sits centered (`BottomCenter`) in portrait and wide foldable screens for easy two-thumb reach.
  - Automatically shifts to `BottomEnd` on compact phone landscape mode to prevent vertical control clipping.

---

## 🏗️ Architecture & Project Structure

The project strictly follows **Clean Architecture** and **MVI**:

```
Logger/
├── androidApp/                     # Android application module
│   └── src/main/
│       ├── AndroidManifest.xml     # Configured for orientation & foldable screen layout
│       └── kotlin/com/asura/logger/MainActivity.kt
├── iosApp/                         # iOS application module (SwiftUI wrapper)
│   └── iosApp/
│       ├── iOSApp.swift            # SwiftUI App entry point
│       └── ContentView.swift       # Hosts ComposeUIViewController
├── desktopApp/                     # Desktop JVM application module (macOS, Windows, Linux)
│   └── src/jvmMain/kotlin/com/asura/logger/Main.kt
├── webApp/                         # Web application module (Wasm-JS & JS)
│   └── src/wasmJsMain/ & jsMain/
└── shared/                         # Core multiplatform shared module
    └── src/commonMain/kotlin/com/asura/logger/
        ├── core/                   # Shared architectural foundations
        │   ├── mvi/                # MviViewModel, UiState, UiIntent, UiEffect
        │   └── util/               # TimeFormatter (formatting mm:ss.SS, hh:mm:ss)
        ├── domain/                 # Pure domain business logic (no UI/framework dependencies)
        │   ├── model/              # Lap, StopwatchRecord, TimerDuration entities
        │   ├── repository/         # StopwatchRepository, TimerRepository interfaces
        │   └── usecase/            # CalculateLapSplits, NormalizeDuration, CalculateTimerProgress
        ├── data/                   # Data sources and repository implementations
        │   ├── datasource/         # MonotonicTimeSourceProvider
        │   └── repository/         # StopwatchRepositoryImpl, TimerRepositoryImpl
        └── presentation/           # UI layer (Compose Multiplatform & MVI)
            ├── navigation/         # FloatingNavBar, AppScreen
            ├── stopwatch/          # StopwatchScreen, StopwatchViewModel, StopwatchContract
            │   └── components/     # StopwatchClockCard, StopwatchControls, StopwatchLapList
            └── timer/              # TimerScreen, TimerViewModel, TimerContract
                └── components/     # TimerDurationPicker, WheelPicker, TimerProgressRing, TimerControls, TimerPresetChips
```

---

## 🚀 How to Run Each Variant

### Prerequisites
- **JDK 17 or higher** (e.g. Android Studio JBR or OpenJDK 17+)
- **Android Studio** (Ladybug / Meerkat or newer) with KMP plugins installed
- **Xcode** (15+ for iOS compilation on macOS)

---

### 🤖 1. Android App
Run on an Android emulator or connected physical device (supports foldables & tablets):

```bash
# Build and install debug APK to connected device/emulator
./gradlew :androidApp:installDebug

# Or build debug APK only
./gradlew :androidApp:assembleDebug
```

---

### 🍏 2. iOS App
Run on an iOS Simulator or connected iPhone:

1. Open `iosApp/iosApp.xcodeproj` in **Xcode**.
2. Select your target simulator (e.g., iPhone 15 Pro, iPad Pro) or connected device.
3. Press **Run (⌘ + R)**.

Alternatively, execute via Gradle to verify iOS metadata compilation:
```bash
./gradlew :shared:compileIosMainKotlinMetadata
```

---

### 💻 3. Desktop App (macOS, Windows, Linux)
Run the desktop Compose Multiplatform application natively:

```bash
# Standard Desktop Run
./gradlew :desktopApp:run
```

---

### 🌐 4. Web Application (Wasm & JS)
Run in modern browsers (Chrome, Edge, Firefox, Safari):

```bash
# Wasm-JS target (Recommended: high-performance WebAssembly)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# JavaScript target (Standard browser support)
./gradlew :webApp:jsBrowserDevelopmentRun
```

---

## 🧪 Composable Previews

All UI components and screens include interactive `@Preview` functions covering:
- **Portrait Idle & Running states**
- **Dual-Pane Landscape viewports** (`widthDp = 800, heightDp = 400`)
- **Foldable Unfolded Inner Screen viewports** (`widthDp = 700, heightDp = 750`)

You can preview any component directly in Android Studio / Fleet without deploying to a physical device.

---

## 🛠️ Verification & Compile Checks

To verify that all platform targets build cleanly from the command line:

```bash
./gradlew compileCommonMainKotlinMetadata compileKotlinJvm compileKotlinJs :androidApp:assembleDebug
```