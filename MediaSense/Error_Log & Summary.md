# 📘 Project Error Log

A daily record of errors encountered, fixes applied, and learnings during development.

---

# 📅 Day 1 — Gradle, SDK & Resource Issues

## ❌ Errors Encountered

### 1️⃣ Syntax Error in build.gradle.kts
**Error:** Unexpected tokens (use ';' to separate expressions)

**Analysis:**  
Used Groovy-style syntax in a Kotlin DSL (.kts) file.

**Fix:**
```
id("com.android.application")
compileSdk = 35
```

---

### 2️⃣ Unresolved Reference: kotlinOptions
**Error:** kotlinOptions / jvmTarget not found

**Analysis:**  
Kotlin plugin was not applied.

**Fix:**
```
plugins {
id("org.jetbrains.kotlin.android")
}
```

---

### 3️⃣ Plugin Version Conflict
**Error:** Plugin already on classpath

**Analysis:**  
Version was defined twice (root + module).

**Fix:**  
Defined version in root using `apply false` and removed from app-level file.

---

### 4️⃣ Duplicate Extension: kotlin
**Error:** Extension 'kotlin' already exists

**Analysis:**  
Project is Java-based (.java), Kotlin plugin unnecessary.

**Fix:**  
Removed Kotlin plugin and kotlinOptions block.

---

### 5️⃣ Incompatible compileSdk
**Error:** Requires API 36+

**Analysis:**  
Modern AndroidX libraries need latest SDK.

**Fix:**
```
compileSdk = 36
targetSdk = 35
```

---

### 6️⃣ Android Resource Linking Failed
**Error:** attr/colorBackground not found

**Analysis:**  
Used wrong namespace.

**Fix:**
```
?android:attr/colorBackground
```

---

## 🧠 Summary of Day 1

- 😵 Faced a **huge number of errors**
- ⏳ Took ~5–6 hours to fix everything
- 💻 Struggled with Git & setup due to no prior practice
- 🤯 Even hit ChatGPT daily limit 😅
- ✅ Finally got the app running successfully

---

# 📅 Day 2 — Resource, Java & Layout Issues

## ❌ Errors Encountered

### 1️⃣ Android Resource Linking Error
**Error:** attr/colorBackground not found

**Cause:**  
Wrong attribute reference in XML

**Fix:**
```
?android:attr/colorBackground
```

---

### 2️⃣ Java Version Warning
**Error:** Java 8 deprecated

**Cause:**  
Old Java version used

**Fix:**
```
compileOptions {
sourceCompatibility = JavaVersion.VERSION_11
targetCompatibility = JavaVersion.VERSION_11
}
```

---

### 3️⃣ Layout Attribute Typo
**Error:** Invalid constraint attribute

**Cause:**  
Wrong attribute name

**Fix:**
```
app:layout_constraintEnd_toEndOf="parent"
```

---

### 4️⃣ Constraint Layout Issues
**Issue:** Misaligned / circular constraints

**Fix:**  
Restructured layout properly:

- 🎵 Card 1 → Now Playing
- 🎮 Card 2 → Controls
- 📂 Card 3 → Open File (top)

---

## 🧠 Summary of Day 2

- 😌 Much easier than Day 1
- ⚡ Fixed issues quickly
- 🎨 Learned layout positioning
- 📱 Learned file transfer to emulator
- 🚀 Smooth progress overall

---

## 📊 Overall Progress

- 🟢 Day 1 → Foundation Complete
- 🟢 Day 2 → Audio + Fixes Complete
- 🟡 Day 3 → Next (Video Player)
- 🟡 Day 4 → Pending (Sensors)

---

## 📸 Screenshots

📁 Screenshots are available in the `Screenshot/` sub-folder  