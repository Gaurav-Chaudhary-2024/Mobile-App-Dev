# 🚀📱 MediaSense
### Smart Media & Sensor Hub in One App

✨ **MediaSense** is a modern Android application that seamlessly combines:

- 🎧 Audio Playback
- 🎥 Video Streaming
- 📡 Sensor Monitoring

—all inside one clean, **Material 3 powered interface**.

---

## 🌟 Why MediaSense?

- 💡 Demonstrates multiple Android capabilities in one app
- ⚡ Smooth navigation with Bottom Navigation UI
- 🎨 Built using Material 3 (Light + Dark Mode)
- 🧩 Modular architecture for scalability

---

## 🧭 App Sections

| Feature             | Description                          |
|---------------------|--------------------------------------|
| 🎧 **Audio Player** | Play audio files from device storage |
| 🎥 **Video Player** | Stream videos using URL              |
| 📡 **Sensors**      | Live data from device sensors        |

---

## 🗂️ Project Structure
MediaSense/
├── 📁 app # Main application code
├── 📁 gradle # Build system
├── 📄 build.gradle # Configurations
├── 📄 settings.gradle
└── 📄 README.md # Documentation

---

# 🗓️ Development Progress

## 🧱 Day 1 — Foundation Setup

### 🎯 Goal
Build the complete project skeleton so future features can plug in easily.

### ✅ What We Implemented
- 🎨 Material 3 Theme (Light + Dark)
- 🔻 Bottom Navigation (3 Tabs)
- 🧩 Fragment-based architecture
- 📦 All dependencies added upfront

### 🏗️ Architecture
- 🏠 MainActivity → Hosts navigation
- 🎧 AudioFragment → Placeholder (upgraded in Day 2)
- 🎥 VideoFragment → Placeholder
- 📡 SensorsFragment → Placeholder

⚡ Fragments are reused → better performance

### 📄 Key Files
- activity_main.xml → Layout with Bottom Navigation
- MainActivity.java → Fragment switching
- fragment_audio.xml → Placeholder UI
- fragment_video.xml → Placeholder UI
- fragment_sensors.xml → Placeholder UI
- build.gradle → Dependencies
- AndroidManifest.xml → Permissions

### 📸 Screenshots (Day 1)
📁 Screenshots available in the `Screenshot/` sub-folder

---

## 🎧 Day 2 — Audio Player

### 🎯 Goal
Convert AudioFragment into a fully functional audio player

### 🚀 What We Built
- 📂 Pick audio from device
- ▶️ Play / ⏸ Pause / ⏹ Stop / 🔁 Restart
- 🎚 SeekBar with live timestamps
- 🎵 File name display

### 🧩 UI Design (Material 3 - Cards)
- 🎵 Card 1 → Music icon + file name
- 🎚 Card 2 → SeekBar + controls
- 📂 Card 3 → Open file button

### ⚙️ ExoPlayer Integration
- ✅ Better format support
- ✅ Smooth playback
- ✅ Modern API (Media3)

### 📂 File Picker
- Uses ActivityResultLauncher
- Opens system file manager
- Filters: `audio/*`

### 🔐 Permission Handling

| Android Version | Permission Required        |
|----------------|--------------------------|
| 📱 Android 13+ | READ_MEDIA_AUDIO         |
| 📱 Android 6–12 | READ_EXTERNAL_STORAGE   |
| 📱 Below 6     | ❌ Not required          |

✔️ Permissions handled at runtime

### 🎚 SeekBar System
- ⏱ Updates every 500ms
- 👆 User can drag to seek
- ⏳ Shows current + total duration

### 🎮 Playback Controls

| Action     | Function               |
|------------|------------------------|
| ▶️ Play    | `exoPlayer.play()`     |
| ⏸ Pause    | `exoPlayer.pause()`    |
| ⏹ Stop     | `stop()` + `seekTo(0)` |
| 🔁 Restart | `seekTo(0)` + `play()` |
| 📂 Open    | Launch file picker     |

### 🧠 Lifecycle Management
- onPause() → ⏸ Pause
- onDestroyView() → 🧹 Release player

✅ Prevents memory leaks  
✅ Smooth performance

### 📊 Day 2 Progress

| Feature               | Status       |
|----------------------|-------------|
| 🎧 Audio Playback     | ✅ Completed |
| 📂 File Picker        | ✅ Completed |
| 🎚 SeekBar            | ✅ Completed |
| 🎮 Controls           | ✅ Completed |
| 🧠 Lifecycle Handling | ✅ Completed |

### 📸 Screenshots (Day 2)
📁 Screenshots available in the `Screenshot/` sub-folder

---

## 🚧 Current Status

- 🟢 Foundation Complete
- 🟢 Audio Player Complete
- 🟡 Video Player → Next
- 🟡 Sensors → Pending

---

## 🔮 Next Steps

### 🎥 Day 3 — Video Player
- Stream video via URL
- ExoPlayer integration

### 📡 Day 4 — Sensors
- Accelerometer
- Light
- Proximity

---

## 👤 Author

💻 **Gaurav Chaudhary**