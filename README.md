# Calculator — Android Studio (Java)

A simple, native Android calculator app built with Java in Android Studio. This repository contains the source code for a basic calculator that demonstrates typical arithmetic operations, a clear UI, and state handling across device rotations.

Languages: Java (100%)

## Features

- Basic arithmetic: addition, subtraction, multiplication, division
- Decimal support
- Clear (C) and All Clear (AC)
- Simple and responsive UI suitable for phones
- Handles simple error states (e.g., divide by zero)
- Preserves current calculation on configuration changes (rotation)

## Screenshots

(Add screenshots to the `docs/` folder or the repository root and reference them here.)

Example:
![Calculator screenshot](docs/ss.jpeg)

---

## Getting started

### Requirements

- Android Studio (Arctic Fox or later recommended)
- JDK 30+ (as configured by Android Studio)
- Android SDK (API level matching your project's compileSdkVersion)
- A device or emulator running Android (check your project's minSdkVersion)

## Build and run

Open the project in Android Studio:

1. File → Open... → select the project's root folder.
2. Let Android Studio sync and download dependencies.
3. Select a target device (emulator or physical device).
4. Run (Shift+F10 or the Run ▶ button).

## Project structure (typical)

- app/
  - src/
    - main/
      - java/  — Java source files (activities, view models, etc.)
      - res/   — layouts, drawables, strings, styles
      - AndroidManifest.xml
- gradle/
- build.gradle (project)
- app/build.gradle (module)

## App usage

- Tap numeric buttons to enter numbers.
- Tap arithmetic operator buttons (+, −, ×, ÷) to build an expression.
- Tap `=` to evaluate.
- Use `C` to clear the current entry and `AC` to reset all.

---

## Error handling

- Division by zero displays an error message or resets the current operation (implementation-specific).
- Invalid input is sanitized where applicable.

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes and commit: `git commit -m "Add feature"`
4. Push to your fork: `git push origin feature/your-feature`
5. Open a Pull Request describing your changes.

Please include:
- A clear description of the change
- The devices / API levels you tested on
- Screenshots (if UI changes)


## Contact

Maintainer: GDJ2001  
Repository: https://github.com/GDJ2001/Calculator-Android-Studios
