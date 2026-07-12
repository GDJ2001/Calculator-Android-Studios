# Changelog

All notable changes to the Android Calculator are documented here.

## [1.0.0] - 2026-07-12

### Added
- First stable release of the Android calculator.
- Standard calculator optimized for portrait orientation.
- Scientific calculator activated automatically in landscape orientation (no manual switch).
- Responsive layouts for compact and regular phone screens.
- Premium light and dark themes with a clear button hierarchy.
- Calculation state preserved across device rotation (expression, result, angle mode).

### Fixed
- Global orange-button rendering bug: text keys rendered orange because `MaterialButton` ignored the per-key `android:background` drawables and fell back to the theme `colorPrimary`. Buttons now use `AppCompatButton` so each key paints its own surface; only the equals key keeps the orange accent.
- Startup `NullPointerException` when `btnPower` (landscape-only) was bound in portrait. Scientific keys are now bound only when present.
- Compressed landscape scientific layout rebuilt as a single 8-column by 5-row grid of capsule keys.
- Reciprocal (1/x) and sign (±) scientific operations implemented and unit tested.

### Calculator functions
- Addition, subtraction, multiplication, division.
- Percentage.
- Decimal calculations.
- Backspace and clear all.
- Trigonometric functions: sin, cos, tan, sin⁻¹, cos⁻¹, tan⁻¹ (RAD/DEG aware).
- Logarithms: ln, log.
- Powers and roots: x², xʸ, √.
- Constants: π, e.
- Factorial (n!).
- Reciprocal (1/x) and sign toggle (±).
- Parentheses.
