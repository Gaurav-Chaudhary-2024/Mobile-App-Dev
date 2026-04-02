# 📸 SnapVault — Day 1

A modern Android camera app built using CameraX and Material 3.  
Day 1 establishes the full foundation — from live camera preview to saving photos and navigation structure.

---

## 🚀 Day 1 Overview

We built the **core architecture of SnapVault** in a single day:

✅ Live camera preview  
✅ Capture and save photos  
✅ Choose custom save folder  
✅ Bottom navigation (Camera + Gallery)  
✅ Material 3 Teal theme  
✅ Placeholder screens for future features

---

## 🧠 Big Picture

SnapVault is designed as a **modular, scalable media app**:

- 📷 Camera (Day 1)
- 🖼️ Gallery (Day 2)
- 🔍 Image Details (Day 3)

Day 1 focuses entirely on building a **strong camera foundation**.

---

## 🛠️ Tech Stack

- Java (Android)
- CameraX (Modern Camera API)
- Material 3 UI
- Glide (for future image loading)
- AndroidX Libraries

---

## 📦 Dependencies

### 📷 CameraX
- `camera-core` → core logic
- `camera-camera2` → hardware connection
- `camera-lifecycle` → lifecycle-aware camera
- `camera-view` → PreviewView (UI)

💡 Why CameraX?
- Camera2 = complex (~500+ lines)
- CameraX = simple (~50 lines), same result

---

### 🖼️ Glide
- Loads images efficiently
- Handles caching
- Prevents memory issues

---

## 🔐 Permissions Handling

Supports **ALL Android versions**:

- `CAMERA` → access camera
- `READ_EXTERNAL_STORAGE` (≤ Android 12)
- `WRITE_EXTERNAL_STORAGE` (≤ Android 9)
- `READ_MEDIA_IMAGES` (Android 13+)

💡 Ensures compatibility across devices.

---

## 🎨 UI & Theme

- Material 3 design
- Teal/Green color theme (unique from MediaSense)
- Auto Light/Dark mode support

---

## 🏗️ App Architecture

```
SnapVault/
├── 📁 app
│   ├── 📁 java/com/example/snapvault
│   │   ├── MainActivity.java
│   │   ├── CameraFragment.java
│   │   ├── GalleryFragment.java (Day 2)
│   │   └── ImageDetailActivity.java (Day 3)
│   ├── 📁 res/layout
│   │   ├── activity_main.xml
│   │   ├── fragment_camera.xml
│   │   ├── fragment_gallery.xml
│   │   └── activity_image_detail.xml
│   └── 📁 res/values
│       ├── colors.xml
│       └── themes.xml
├── 📄 AndroidManifest.xml
└── 📄 README.md
```

---

## 📱 UI Structure

```
┌─────────────────────────┐
│  MaterialToolbar        │
├─────────────────────────┤
│  FragmentContainerView  │
│   (Camera / Gallery)    │
├─────────────────────────┤
│  BottomNavigationView   │
└─────────────────────────┘
```

---

## 📷 Camera Screen

```
┌─────────────────────────┐
│     PreviewView         │  ← Live camera feed
│                         │
├─────────────────────────┤
│ Saving to: SnapVault    │
│                         │
│ [Choose Folder][Capture]│
└─────────────────────────┘
```

---

## ⚙️ Core Implementation

### 📍 Camera Setup (CameraX)

- Uses `ProcessCameraProvider`
- Two use cases:
    - Preview → shows live feed
    - ImageCapture → takes photos

- Lifecycle aware:
    - Starts/stops automatically

---

### 📸 Photo Capture

- Filename with timestamp  
  `SnapVault_YYYYMMDD_HHMMSS.jpg`

- Uses **MediaStore**:
    - Works on all Android versions
    - No legacy storage issues
    - Images appear in device gallery

---

### 📁 Folder Selection

- Uses system folder picker
- Saves selected folder URI
- Updates UI dynamically
- Future captures use selected folder

---

### 🔐 Permissions Flow

- Requests camera + storage permissions
- Handles:
    - ✅ Granted → start camera
    - ❌ Denied → show Snackbar

---

## 🔄 Navigation System

- Bottom Navigation:
    - Camera
    - Gallery

- Smart optimization:
```java
if (fragment == activeFragment) return;
```
✔ Prevents unnecessary reloads

---

## 🧪 Placeholder Features

### 🖼️ Gallery (Day 2)
- Image grid (RecyclerView)
- Folder selection
- Image preview

### 🔍 Image Details (Day 3)
- Full image view
- Metadata (size, date, path)
- Delete with confirmation

---

## 📊 Summary

| Component | Purpose |
|----------|--------|
| CameraX | Camera preview + capture |
| MediaStore | Safe image saving |
| PreviewView | Live camera feed |
| Navigation | Fragment switching |
| Glide | Future image loading |

---

## ✅ Status

✔ Camera fully working  
✔ UI complete  
✔ Navigation implemented  
✔ Ready for Day 2

---

## 👨‍💻 Developer

💻 **Gaurav Chaudhary**

Built as part of Android learning journey 🚀