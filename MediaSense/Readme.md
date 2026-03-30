# 🚀📱 MediaSense
### Smart Media & Sensor Hub in One App

✨ **MediaSense** is a modern Android application that combines:

- 🎧 Audio Playback
- 🎥 Video Streaming
- 📡 Sensor Monitoring

—all inside a clean, **Material 3 powered UI**.

---

## 🌟 Features

- 🎧 Play audio from device storage
- 🎥 Stream videos via URL (MP4, HLS, DASH)
- 📡 Real-time sensor data (Accelerometer, Light, Proximity)
- 🎨 Material 3 UI (Modern + Responsive)
- 🔻 Bottom Navigation with smooth fragment switching

---

## 🧭 App Sections

| Feature             | Description                          |
|---------------------|--------------------------------------|
| 🎧 Audio Player     | Play local audio files               |
| 🎥 Video Player     | Stream video from URL                |
| 📡 Sensors Dashboard| Live device sensor data              |

---

## 🗂️ Project Structure
```
MediaSense/
├── 📁 app # Main application code
├── 📁 gradle # Build system
├── 📄 build.gradle # Configurations
├── 📄 settings.gradle
└── 📄 README.md # Documentation
```

---

# 🗓️ Development Journey

---

## 🧱 Day 1 — Foundation Setup

### 🎯 Goal
Build the complete app structure and setup environment.

### ✅ Achievements
- 🎨 Material 3 theme setup
- 🔻 Bottom Navigation (3 tabs)
- 🧩 Fragment architecture
- 📦 Dependencies added (Material 3 + ExoPlayer)
- 🔐 Permissions configured

### 🏗️ Architecture
- MainActivity → Hosts navigation
- AudioFragment → Audio module
- VideoFragment → Video module
- SensorsFragment → Sensor module

---

## 🎧 Day 2 — Audio Player

### 🚀 Features Implemented
- 📂 Audio file picker (device storage)
- ▶️ Play / ⏸ Pause / ⏹ Stop / 🔁 Restart
- 🎚 SeekBar with timestamps
- 🎵 File name display

### ⚙️ Tech Used
- Media3 ExoPlayer
- ActivityResultLauncher
- Runtime permissions

### 🔐 Permissions

| Android Version | Permission |
|----------------|-----------|
| 13+            | READ_MEDIA_AUDIO |
| 6–12           | READ_EXTERNAL_STORAGE |
| Below 6        | Not required |

### 🧠 Key Learning
- Lifecycle handling (release player)
- Real-time UI updates (Handler + SeekBar)

---

## 🎥 Day 3 — Video Player + Sensors

### 🎬 Video Player

#### 🚀 Features
- 🌐 Stream video via URL
- ▶️ Full playback controls
- 🎚 SeekBar with timestamps
- ⏳ Buffering indicator
- ❗ Error handling (invalid URL, network issues)

#### ⚙️ Tech
- ExoPlayer + PlayerView
- MediaItem (URI-based streaming)

#### ✅ Supported Formats
- MP4
- HLS (.m3u8)
- DASH

---

### 📡 Sensor Dashboard

#### 📊 Sensors Implemented

| Sensor          | Output                     |
|----------------|---------------------------|
| 📱 Accelerometer | X, Y, Z (m/s²)           |
| 💡 Light        | Ambient light (lux)      |
| 📏 Proximity    | Distance (cm)            |

#### ⚙️ Features
- 🔁 Real-time updates
- 🔋 Battery-efficient lifecycle handling
- ⚠️ Sensor availability check

---

## 📊 Current Status

- 🟢 Foundation Complete
- 🟢 Audio Player Complete
- 🟢 Video Player Complete
- 🟢 Sensors Dashboard Complete 🎉

---

## 📸 Screenshots

📁 Screenshots available in the `Screenshot/` folder

---

## 🔮 Future Improvements

- 🎛 Advanced media controls
- 🌐 Online streaming enhancements
- 📊 More sensors (Gyroscope, Magnetometer)
- 🎨 UI animations & polish

---

## 🛠️ Tech Stack

- Java (Android)
- Material 3
- ExoPlayer (Media3)
- Android SDK (API 36)

---

## 👤 Author

💻 **Gaurav Chaudhary**