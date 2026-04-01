📘 CurrencyCovertor — Error Log

A record of errors encountered and fixes applied during development.

📅 Day 1 — Gradle & Dependency Configuration Issues
Errors Encountered
1. Kotlin DSL Syntax Errors in build.gradle.kts

Error: Unexpected tokens due to invalid syntax

Analysis:
The build.gradle.kts file was written using Groovy-style syntax (single quotes and missing parentheses). Kotlin DSL requires strict syntax rules.

Fix:
```
implementation("dependency:version")
```
2. Misplaced Dependencies in Root Build File

Issue:
A dependencies block was incorrectly added to the root-level build.gradle.kts file. In standard Android projects, dependencies should be declared in the module-level file (app/build.gradle.kts) or managed via Version Catalog.

Analysis:
This caused improper dependency resolution and project structure issues.

Fix:

Step 1: Updated Version Catalog (gradle/libs.versions.toml)
```
appcompat = "1.6.1"
material = "1.9.0"
constraintlayout = "2.1.4"
```
Step 2: Removed dependencies block from root build.gradle.kts

Step 3: Used Version Catalog references in app module
```
implementation(libs.appcompat)
implementation(libs.material)
implementation(libs.constraintlayout)
```
Summary of the Day
- ⚙️ Faced issues mainly with Gradle build and dependency configuration
- 🔧 Spent time fixing Kotlin DSL and project structure
- ✅ Rest of the development process worked smoothly
- 😮 Surprisingly fewer issues outside Gradle
- 👍 Overall a positive and efficient debugging experience

🚀 Final Status

Project structure corrected and Gradle configuration successfully fixed. Dependencies are now properly managed using Version Catalog.