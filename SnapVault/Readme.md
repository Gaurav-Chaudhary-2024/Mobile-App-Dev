# 📸 SnapVault

A modern Android media app built with CameraX and Material 3.  
SnapVault allows users to capture photos, manage them in a gallery, and view detailed image metadata — all with a clean and optimized UI.

---

## 🚀 Features (Day 1 + Day 2)

### 📷 Camera (Day 1)
- Live camera preview using CameraX (PreviewView)
- Capture photos with low latency
- Save images using MediaStore (works on all Android versions)
- Custom folder selection
- Timestamp-based file naming

---

### 🖼️ Gallery (Day 2)
- Folder picker to load images
- RecyclerView 3-column grid layout
- Efficient image loading using Glide
- Smooth scrolling and caching

---

### 🔍 Image Details (Day 2)
- Full image preview
- Displays metadata:
    - File name
    - File path
    - File size
    - Date created
- Clean detail screen UI

---

### 🗑️ Delete Feature
- Delete images directly from detail screen
- Confirmation dialog (AlertDialog)
- Auto return to gallery after deletion

---

### 🎨 UI & Experience
- Material 3 design system
- Teal/Green theme (distinct from MediaSense)
- Light/Dark mode support
- Bottom navigation (Camera | Gallery)

---

## 🧠 Architecture Overview

SnapVault follows a modular structure:

- 📷 Camera → Capture images
- 🖼️ Gallery → Display images
- 🔍 Details → Inspect + manage images

Each feature is isolated into fragments/activities for scalability.

---

## 🗂️ Project Structure

```
SnapVault/
├── 📁 app
│   ├── 📁 java/com/example/snapvault
│   │   ├── MainActivity.java
│   │   ├── CameraFragment.java
│   │   ├── GalleryFragment.java
│   │   ├── ImageAdapter.java
│   │   └── ImageDetailActivity.java
│   ├── 📁 res/layout
│   │   ├── activity_main.xml
│   │   ├── fragment_camera.xml
│   │   ├── fragment_gallery.xml
│   │   ├── item_image.xml
│   │   └── activity_image_detail.xml
│   ├── 📁 res/drawable
│   │   └── ic_back.xml
│   └── 📁 res/values
│       ├── colors.xml
│       ├── strings.xml
│       └── themes.xml
├── 📄 AndroidManifest.xml
└── 📄 README.md
```

---

## ⚙️ Core Concepts

### 📷 CameraX Integration
- Simplified modern camera API
- Lifecycle-aware (auto start/stop)
- Uses:
    - Preview → live feed
    - ImageCapture → photo capture

---

### 💾 MediaStore Usage
- Saves images safely across all Android versions
- No legacy storage issues
- Images appear in device gallery automatically

---

### 🖼️ RecyclerView + Adapter
- Custom `ImageAdapter`
- Displays images in grid format
- Uses Glide for optimized loading

---

### 🔄 Navigation Flow

```
Camera → Capture Photo
       ↓
Gallery → Load Folder → Show Images
       ↓
Tap Image → Open Details
       ↓
Delete → Confirm → Return to Gallery
```

---

## 🔐 Permissions Handling

Supports all Android versions:

- `CAMERA`
- `READ_EXTERNAL_STORAGE` (≤ Android 12)
- `WRITE_EXTERNAL_STORAGE` (≤ Android 9)
- `READ_MEDIA_IMAGES` (Android 13+)

---

## 📱 UI Layout

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

## 🧪 Completed Flow

✔ Capture photo  
✔ Save to selected folder  
✔ Open gallery  
✔ View images in grid  
✔ Open image details  
✔ Delete image with confirmation

---

## 🛠️ Tech Stack

- Java (Android)
- CameraX
- RecyclerView
- Glide
- Material 3 UI
- AndroidX Libraries

---

## 📊 Status

✔ Day 1 Complete — Camera System  
✔ Day 2 Complete — Gallery + Details  
🚧 Day 3 Pending — Advanced features

---

## 👨‍💻 Developer

💻 **Gaurav Chaudhary**

Built as part of Android development learning journey 🚀