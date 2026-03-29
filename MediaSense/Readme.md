🚀📱 MediaSense
Smart Media & Sensor Hub in One App

✨ MediaSense is a modern Android application that seamlessly combines
🎧 Audio Playback, 🎥 Video Streaming, and 📡 Sensor Monitoring
— all inside one clean, Material 3 powered interface.

🌟 Why MediaSense?

💡 Built to demonstrate multiple Android capabilities in one unified app
⚡ Smooth navigation with Bottom Navigation UI
🎨 Designed using Material 3 (Light + Dark Mode)
🧩 Modular architecture for easy scalability

🧭 App Sections
| Feature             | Description                          |
| ------------------- | ------------------------------------ |
| 🎧 **Audio Player** | Play audio files from device storage |
| 🎥 **Video Player** | Stream videos using URL              |
| 📡 **Sensors**      | Live data from device sensors        |

🗂️ Project Structure

MediaSense/
├── 📁 app            💻 Main application code
├── 📁 gradle         ⚙️ Build system
├── 📄 build.gradle   📦 Configurations
├── 📄 settings.gradle
└── 📄 README.md      📘 Documentation

[//]: # (MediaSense/)

[//]: # ()
[//]: # ( ├── 📁 .gradle        ⚙️ Auto-generated)

[//]: # ()
[//]: # ( ├── 📁 .idea          🧠 IDE config)

[//]: # ()
[//]: # ( ├── 📁 app            💻 Main application code)

[//]: # ()
[//]: # ( ├── 📁 gradle         🔧 Build system)

[//]: # ()
[//]: # ( ├── 📄 build.gradle   📦 Project config)

[//]: # ()
[//]: # ( ├── 📄 settings.gradle)

[//]: # ()
[//]: # ( ├── 📄 gradlew)

[//]: # ()
[//]: # ( ├── 📄 gradlew.bat)

[//]: # ()
[//]: # ( └── 📄 .gitignore)

🗓️ Development Progress
🧱 Day 1 — Foundation Setup
🎯 Goal

Build the complete project skeleton so future features can plug in easily.

✅ What We Implemented
🎨 Material 3 Theme (Light + Dark mode)
🔻 Bottom Navigation (3 Tabs)
🧩 Fragment-based architecture
📦 All dependencies added upfront
🏗️ Architecture Setup
🏠 MainActivity → Hosts navigation
🎧 AudioFragment → Placeholder (now upgraded in Day 2)
🎥 VideoFragment → Placeholder
📡 SensorsFragment → Placeholder

⚡ Fragments are reused → better performance

📄 Key Files (Day 1)
activity_main.xml → Layout with Bottom Navigation
MainActivity.java → Handles fragment switching
fragment_audio.xml → Placeholder UI
fragment_video.xml → Placeholder UI
fragment_sensors.xml → Placeholder UI
build.gradle → All dependencies added
AndroidManifest.xml → Required permissions

📸 Day 1 Preview
The Screenshot of the outputs have been saved in the Screenshot Sub-Folder

🎧 Day 2 — Audio Player
🎯 Goal

Convert AudioFragment into a fully functional audio player

🚀 What We Built
📂 Pick audio from device
▶️ Play / ⏸ Pause / ⏹ Stop / 🔁 Restart
🎚 SeekBar with live timestamps
🎵 File name display
🧩 Files Updated
📄 fragment_audio.xml

🔄 Redesigned into Material 3 UI (3 Cards)

🎵 Card 1 → Music icon + file name
🎚 Card 2 → SeekBar + controls
📂 Card 3 → Open file button
📄 AudioFragment.java

💡 Complete audio player implementation

🎧 ExoPlayer Integration

Using Media3 ExoPlayer:

✅ Better format support
✅ Smooth playback
✅ Modern API (Google supported)
📂 File Picker
Uses ActivityResultLauncher
Opens system file manager
Filters: audio/*
🔐 Permission Handling
Android Version	Permission
📱 Android 13+	READ_MEDIA_AUDIO
📱 Android 6–12	READ_EXTERNAL_STORAGE
📱 Below 6	❌ Not required

✔️ Checked at runtime

🎚 SeekBar System
⏱ Updates every 500ms
00:20 ──────●────── 05:17
👆 User can drag to seek
⏳ Shows current + total duration

🎮 Playback Controls
| Action     | Function               |
| ---------- | ---------------------- |
| ▶️ Play    | `exoPlayer.play()`     |
| ⏸ Pause    | `exoPlayer.pause()`    |
| ⏹ Stop     | `stop()` + `seekTo(0)` |
| 🔁 Restart | `seekTo(0)` + `play()` |
| 📂 Open    | Launch file picker     |

🧠 Lifecycle Management
onPause() → ⏸ Pause
onDestroyView() → 🧹 Release player

✅ Prevents memory leaks
✅ Smooth performance

📄 strings.xml

➕ Added all UI strings for audio feature

📊 Day 2 Progress
| Feature               | Status      |
| --------------------- | ----------- |
| 🎧 Audio Playback     | ✅ Completed |
| 📂 File Picker        | ✅ Completed |
| 🎚 SeekBar            | ✅ Completed |
| 🎮 Controls           | ✅ Completed |
| 🧠 Lifecycle Handling | ✅ Completed |

📸 Day 2 Preview
The Screenshot of the outputs have been saved in the Screenshot Sub-Folder

🚧 Current Status

🟢 Foundation Complete
🟢 Audio Player Complete
🟡 Video Player → Next
🟡 Sensors → Pending

🔮 Next Steps
🎥 Day 3 — Video Player
Stream video via URL
ExoPlayer integration

📡 Day 4 — Sensors
Accelerometer
Light
Proximity

👤 Author
💻 Gaurav Chaudhary
