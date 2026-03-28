📘 Project Error Log

A daily record of errors encountered, fixes applied, and learnings during development.

📅 Day 1 — Gradle, SDK & Resource Issues
Errors Encountered

1)Syntax Error in build.gradle.kts

Error: Unexpected tokens (use ';' to separate expressions on the same line)

Analysis: This occurred because Groovy-style syntax (e.g., id 'com.android.application' or compileSdk 35) was used inside a file with a .kts extension. Kotlin DSL requires strict Kotlin syntax, specifically parentheses and double quotes.

Fix:
id("com.android.application")
compileSdk = 35

2)Unresolved Reference: kotlinOptions

Error: Unresolved reference 'kotlinOptions' and Unresolved reference 'jvmTarget'

Analysis: The build script was attempting to configure Kotlin-specific options, but the Kotlin Android plugin was not applied to the module, so the kotlinOptions block was not recognized.

Fix:
plugins {
id("org.jetbrains.kotlin.android")
}

3)Plugin Version Conflict

Error: The request for this plugin could not be satisfied because the plugin is already on the classpath with an unknown version

Analysis: Gradle forbids specifying a version for a plugin in a subproject if the plugin version is already defined in the root project or version catalog.

Fix: Defined the version in the root build.gradle.kts using apply false and removed the version string from the app-level build.gradle.kts.

4)Duplicate Extension: kotlin

Error: Cannot add extension with name 'kotlin', as there is an extension already registered with that name

Analysis: This project is a pure Java project (all source files are .java). Applying the Kotlin plugin to a project with no Kotlin code caused internal conflicts with the existing Java configuration.

Fix: Removed the Kotlin plugin and the kotlinOptions block entirely, simplifying the configuration for a Java-based project.

5)Incompatible compileSdk (AAR Metadata Failure)

Error: Dependency 'androidx.activity:activity:1.13.0' requires libraries to compile against version 36 or later

Analysis: Modern AndroidX libraries (like Media3 dependencies) require the latest Android SDK (API 36 or higher) to compile, even if the app targets a lower version.

Fix:
compileSdk = 36
targetSdk = 35

6)Android Resource Linking Failed

Error: resource attr/colorBackground not found

Analysis: The layout XML files were using ?attr/colorBackground. In the Android framework, the background attribute belongs to the system namespace.

Fix:
?android:attr/colorBackground

Summary of the Day
Learned a lot about error got a hell lot of errors fixed all of them by midnight of 27/03/2026 wanted to start with the setup of GitHub and Git but i was tired so dropped the idea got back to work in the morning and faced a lot of errors during the setup took 5-6 hours took more than i thought but i deserved that i did not watch a video about version control in android studio before so got blessed with so many errors that chatgpt gave up and said daily limit over but in the end got it up and running and now i am working on Day - 2 hope the work is over quickly.

📸 Screenshots

(Add your screenshots from the screenshots folder here)

![Screenshot](./Screenshots/Day_1_audio.png)