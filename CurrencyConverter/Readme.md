# 💱 CurrencyCovertor

A modern and lightweight Android app for seamless currency conversion with a clean UI and smart dark mode support.

---

## ✨ Features

- 💵 Convert between multiple currencies instantly
- 🔁 Accurate and efficient conversion logic
- 🌙 Light / Dark mode toggle
- 💾 Saves user preferences (theme)
- 🎯 Clean Material Design UI

---

## ⚙️ How It Works

### 🔄 Conversion Logic
- All rates are stored as: **1 USD = X currency**
- Conversion steps:
    - Convert input → USD (divide by source rate)
    - Convert USD → target (multiply by target rate)
- ➕ Adding a new currency = just one line in `initRates()`

---

### 🎨 Theme System
- Uses `AppCompatDelegate.setDefaultNightMode()`
- DayNight theme auto-handles colors
- No need for separate light/dark styling
- `recreate()` refreshes UI instantly after toggle

---

### 🧠 Smart Initialisation
- Uses `isInitialising` flag
- Prevents unwanted trigger on app start
- Avoids unnecessary UI reload

---

## 🗂️ Project Structure

```
CurrencyCovertor/
├── 📁 app
│   ├── 📁 java/com/example/currencyconverter
│   │   ├── MainActivity.java
│   │   └── SettingsActivity.java
│   ├── 📁 res/layout
│   │   ├── activity_main.xml
│   │   └── activity_settings.xml
│   └── 📁 res/values
│       ├── strings.xml
│       └── themes.xml
├── 📄 AndroidManifest.xml
└── 📄 README.md
```

---

## 📱 Screens

- 🏠 **Main Screen** → Currency conversion
- ⚙️ **Settings Screen** → Theme toggle

---

## 🛠️ Tech Stack

- Java (Android)
- ConstraintLayout
- SharedPreferences
- AppCompat (Dark Mode)
- Material UI Components

---

## 📌 Highlights

- ⚡ Fast and lightweight
- 🔌 Works offline (static rates)
- 🧩 Easy to extend (add currencies easily)

---

## ✅ Status

✔️ Fully Functional  
✔️ Clean UI  
✔️ Optimized Logic

---

## 👨‍💻 Developer

💻 **Gaurav Chaudhary**

Built as part of Android development learning 🚀