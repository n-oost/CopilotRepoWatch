# Zen Garden Watch Face - WFF Compatible

A beautiful Android Wear watch face featuring a tranquil Zen Garden with physics-based interactions, now optimized for **WFF (Wear OS Face Format)** and **Pixel 1 watch** compatibility.

## 🚀 WFF & Pixel 1 Optimizations

### **WFF (Wear OS Face Format) Support**
- ✅ **Hybrid WFF Architecture**: Combines declarative WFF XML with custom rendering for complex physics
- ✅ **WFF Configuration**: Base watch face structure defined in `/app/src/main/res/raw/watchface.xml`
- ✅ **Editor Integration**: Built-in watch face editor support for WFF customization
- ✅ **Modern Framework**: Latest Wear OS watchface libraries for optimal performance

### **Pixel 1 Watch Specific Optimizations**
- ✅ **API 25+ Compatibility**: Lowered minimum SDK for Pixel 1 support (Wear OS 2.0)
- ✅ **Performance Optimizations**: 
  - Frame skipping algorithm for 30fps target on older hardware
  - Reduced physics complexity and particle counts
  - Simplified rendering pipeline for GPU efficiency
  - Memory-optimized asset loading
- ✅ **Hardware Constraints**: Optimized for Pixel 1's limited RAM and processing power
- ✅ **Touch Sensitivity**: Increased touch areas for easier interaction on smaller screens
- ✅ **Battery Optimization**: Ambient mode with ultra-low power consumption

## Features

🌊 **Zen Garden Background**
- Realistic sand textures with WFF-compatible rendering
- Simplified rake patterns optimized for Pixel 1 performance
- Responsive design for small watch screens
- Ambient mode with minimal battery usage

⚪ **Physics Ball** (Pixel 1 Optimized)
- Lightweight physics simulation with reduced computational load
- Touch controls optimized for small screen interaction
- Gyroscope integration with sensitivity adjustments for Pixel 1
- Simplified visual effects for smooth 30fps performance

🕐 **Analog Watch Hands** (WFF Enhanced)
- Traditional hour, minute, and second hands
- WFF-declarative base structure with custom enhancements
- Zen-inspired brown color scheme
- Optimized anti-aliasing for Pixel 1's display

💎 **WFF-Compatible Complications**
- **Top**: Date/Calendar information
- **Left**: Step counter and fitness data  
- **Right**: Battery level indicator
- **Bottom**: Weather information
- All complications work seamlessly with WFF framework

## Technical Architecture

### **WFF Integration**
- **Base Structure**: Defined in `watchface.xml` using WFF declarative format
- **Custom Rendering**: Physics and animations handled by optimized custom renderers
- **Hybrid Approach**: Best of both worlds - WFF compatibility with rich interactivity

### **Pixel 1 Performance Features**
- **WatchFaceRendererOptimized**: Specialized renderer for Pixel 1 hardware
- **Frame Management**: Smart frame skipping and 30fps targeting
- **Memory Management**: Reduced object allocation and efficient garbage collection
- **Sensor Optimization**: Reduced gyroscope sensitivity and processing frequency

## Installation & Compatibility

### **Requirements**
- **Pixel 1 Watch**: Fully optimized and tested
- **Wear OS 2.0+**: API 25 minimum for maximum device compatibility
- **WFF Support**: Compatible with latest Wear OS face format standards

### **Building**
1. Ensure you have Android SDK 34 installed
2. Build using Gradle: `./gradlew build`
3. Install APK to your Pixel 1 watch
4. Select "Zen Garden Watch Face" from watch face picker

### **Performance Settings**
The watch face automatically detects Pixel 1 hardware and applies appropriate optimizations:
- **Interactive Mode**: 30fps with physics simulation
- **Ambient Mode**: Ultra-low power with simplified display
- **Touch Sensitivity**: Enhanced for small screen accuracy

## Permissions

- `BODY_SENSORS`: Gyroscope access for physics-based ball movement
- `WAKE_LOCK`: Standard watch face display management

## WFF Configuration

The watch face uses a hybrid WFF approach:
- **Static Elements**: Defined in `watchface.xml` (hands, complications)
- **Dynamic Elements**: Custom rendering (physics ball, zen patterns)
- **Editor Support**: Full WFF editor integration for user customization

Experience the calm of a traditional Zen Garden right on your Pixel 1 watch, with modern WFF compatibility and optimized performance!