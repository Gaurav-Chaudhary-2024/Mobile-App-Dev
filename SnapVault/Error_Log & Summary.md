📘 SnapVault — Error Log

A record of errors encountered and fixes applied during development.

📅 Day 1 — Resource Linking Error (AAPT2)
Errors Encountered
1. Android Resource Linking Error

Error: resource attr/colorBackground (aka com.example.snapvault:attr/colorBackground) not found

Affected Files:
activity_main.xml
activity_image_detail.xml
fragment_gallery.xml

Cause

The issue was caused by incorrect referencing of theme attributes in XML layouts.

In Layout Files:
```
android:background="?attr/colorBackground"
```
Explanation:
The ?attr/ prefix tells Android to look for a custom attribute defined in the project (e.g., in attrs.xml).

In Theme File (themes.xml):
```
<item name="android:colorBackground">@color/md_theme_background</item>
```
Explanation:
The android: prefix indicates this is a system attribute being overridden.

Conflict:

Layout expected a custom attribute (colorBackground)
Theme defined a system attribute (android:colorBackground)
No custom attribute existed → resource linking failed
Fix

Updated all layout files to reference the correct system attribute.

Before:
```
android:background="?attr/colorBackground"
```
After:
```
android:background="?android:attr/colorBackground"
```
Summary of Changes

activity_main.xml
Updated root background to ?android:attr/colorBackground

activity_image_detail.xml
Updated root background to ?android:attr/colorBackground

fragment_gallery.xml
Updated root background to ?android:attr/colorBackground

Summary of the Day
- ⏳ Gradle sync took some time initially
- 🧠 Fixed quickly using experience from previous project
- 🔍 Identified attribute mismatch faster this time
- ⚡ Debugging was smoother and more efficient
- 👍 Overall development process was good and stable


🚀 Final Status

Project successfully builds after resolving the resource linking error.
Theme attributes are now correctly mapped to system attributes.