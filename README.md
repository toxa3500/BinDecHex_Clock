# BinDecHexClock for Android

<!-- Ideally, place your app icon here. Copy ic_launcher.png to an 'assets' or 'docs/images' folder -->
<!-- <p align="center"><img src="assets/ic_launcher.png" alt="BinDecHexClock App Icon" width="150"></p> -->

**BinDecHexClock** is a versatile Android application that displays the current time in multiple numeral systems: Binary, Decimal, Hexadecimal, and Roman numerals. It also features a multi-functional Stopwatch and Timer page. The app is designed with a clean, modern interface using AndroidX libraries and Material Components, supporting both light and dark themes.

## Features

*   **Multiple Time Displays:**
    *   ![Binary Icon](https://fonts.gstatic.com/s/i/materialicons/code/v14/gm_grey-24dp.png) **Binary Clock:** Shows hours, minutes, and seconds in binary format.
    *   ![Decimal Icon](https://fonts.gstatic.com/s/i/materialicons/looks_one/v13/gm_grey-24dp.png) **Decimal Clock:** Standard decimal time display (HH:MM:SS).
    *   ![Hexadecimal Icon](https://fonts.gstatic.com/s/i/materialicons/looks_two/v13/gm_grey-24dp.png) **Hexadecimal Clock:** Hours, minutes, and seconds displayed in hexadecimal.
    *   ![Roman Clock Icon](https://fonts.gstatic.com/s/i/materialicons/whatshot/v14/gm_grey-24dp.png) **Roman Clock:** Time shown using Roman numerals.
*   **Live Time Updates:**
    *   ![Update Icon](https://fonts.gstatic.com/s/i/materialicons/update/v17/gm_grey-24dp.png) All clocks update every second to show the current time.
*   **Stopwatch & Timer Page:**
    *   ![Stopwatch Icon](https://fonts.gstatic.com/s/i/materialicons/timer/v16/gm_grey-24dp.png) **Stopwatch Mode:** Standard count-up functionality with start, stop, and reset.
    *   ![Timer Icon](https://fonts.gstatic.com/s/i/materialicons/hourglass_empty/v15/gm_grey-24dp.png) **Timer Mode:** Countdown timer with easy time adjustment (+/- 30s, +/- 3min via short/long press).
    *   ![Alarm Icon](https://fonts.gstatic.com/s/i/materialicons/alarm/v15/gm_grey-24dp.png) System alarm sound plays when the timer reaches zero.
    *   **Multiple Time Formats:** The stopwatch/timer display can also be cycled through Binary, Decimal, Hex, and Roman formats.
*   **Modern UI & UX:**
    *   ![Theme Icon](https://fonts.gstatic.com/s/i/materialicons/brightness_6/v15/gm_grey-24dp.png) Supports both **Light and Dark themes** based on system settings.
    *   Easy navigation using a Bottom Navigation Bar.
    *   Clean and intuitive layout.
*   **Lifecycle Aware Components:** Ensures stability and correct behavior during app lifecycle changes.

## How to Use

1.  **Launch the App:** The default view will typically be one of the clock faces.
2.  **Navigate:** Use the bottom navigation bar to switch between different clock views (Binary, Decimal, Hex, Roman) and the Stopwatch/Timer page.
3.  **Stopwatch/Timer Page:**
    *   Use the **Mode** button to toggle between Stopwatch and Timer modes.
    *   **Start/Stop:** Controls the active function.
    *   **Reset:** Resets the current mode to its default state.
    *   **+/- Buttons:** In Timer mode (when stopped), use these to set the countdown duration. Short press for +/- 30 seconds, long press for +/- 3 minutes.
    *   **Tap Time Display:** Tap the numerical time display on the Stopwatch/Timer page to cycle through Binary, Decimal, Hex, and Roman representations of the elapsed or remaining time.

## Technologies Used

*   **Java**
*   **Android SDK** (minSdkVersion 26 - Android 8.0 Oreo)
*   **AndroidX Libraries:**
    *   AppCompat
    *   ConstraintLayout
    *   Lifecycle (ViewModel, LiveData)
    *   Navigation Components
*   **Material Components for Android:** For UI elements and theming.

---

*This README was generated with assistance from an AI coding assistant.*
