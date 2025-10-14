# AfriStyle AI - African Fashion Virtual Try-On App

AfriStyle AI is a production-ready Android application that enables users to virtually try on African fashion items using advanced AI technology. The app combines computer vision, machine learning, and cultural fashion expertise to provide an immersive virtual try-on experience featuring authentic African clothing styles.

## Features

- 📸 **Photo Capture & Upload**: Take photos or upload existing images for virtual try-on
- 👗 **African Fashion Catalog**: Browse 50+ authentic African fashion items with cultural context
- 🤖 **AI-Powered Try-On**: Realistic virtual try-on using face detection and image blending
- 📱 **Offline Functionality**: Works completely offline after initial setup
- 📚 **Cultural Education**: Learn about the history and significance of African fashion
- 💾 **Try-On History**: Save and manage your virtual try-on results
- 📤 **Export & Share**: Save results to gallery or share on social media

## African Fashion Styles Included

- **Ankara**: West African wax print fabric with geometric patterns
- **Kente**: Traditional Ghanaian silk and cotton fabric with symbolic meanings
- **Dashiki**: Colorful garment with traditional embroidered neckline
- **Kitenge**: East African fabric known for colorful patterns
- **Boubou**: Flowing wide-sleeved robe from West Africa
- **Agbada**: Traditional Nigerian flowing robe with embroidery
- **Kaftan**: Comfortable loose-fitting robe for warm climates
- **Habesha**: Traditional Ethiopian dress with hand-woven cotton

## Technical Architecture

### AI/ML Components
- **Face Detection**: Google ML Kit for real-time face detection and landmark extraction
- **Image Processing**: OpenCV for advanced image manipulation
- **Outfit Fitting**: Custom algorithms for body shape analysis and outfit positioning
- **Image Blending**: Seamless integration of outfits with user photos

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Navigation**: Compose Navigation
- **Async Operations**: Coroutines & Flow

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK API 24+ (Android 7.0+)
- Minimum 4GB RAM for development
- Device or emulator with camera support

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/afristyle-ai.git
   cd afristyle-ai
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Sync Dependencies**
   - Android Studio will automatically prompt to sync Gradle
   - Click "Sync Now" when prompted
   - Wait for all dependencies to download

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Or use command line: `./gradlew installDebug`

### Required Permissions
The app requires the following permissions:
- **Camera**: For taking photos for virtual try-on
- **Storage**: For saving and accessing images
- **Internet**: For initial setup only (app works offline afterward)

## Project Structure

```
app/
├── src/main/java/com/afristyle/ai/
│   ├── ai/                          # AI processing components
│   │   ├── FaceDetectionProcessor.kt
│   │   ├── OutfitFittingEngine.kt
│   │   ├── ImageBlendingProcessor.kt
│   │   └── VirtualTryOnProcessor.kt
│   ├── data/                        # Data layer
│   │   ├── database/               # Room database
│   │   ├── models/                 # Data models
│   │   └── repository/             # Repository implementations
│   ├── di/                         # Dependency injection
│   ├── ui/                         # UI components and screens
│   │   ├── components/             # Reusable UI components
│   │   ├── navigation/             # Navigation setup
│   │   ├── screens/                # App screens
│   │   └── theme/                  # App theming
│   ├── AfriStyleApplication.kt     # Application class
│   └── MainActivity.kt             # Main activity
├── src/main/res/
│   ├── raw/                        # African fashion catalog data
│   ├── values/                     # Colors, strings, dimensions
│   └── xml/                        # Configuration files
└── build.gradle.kts                # App-level build configuration
```

## Performance Specifications

- **App Startup**: Under 3 seconds
- **Photo Processing**: Under 10 seconds on mid-range devices
- **Memory Usage**: Under 200MB during heavy processing
- **APK Size**: Under 50MB after optimization
- **Compatibility**: Android API 24+ (Android 7.0+)

## Building for Release

1. **Generate Signed APK**
   ```bash
   ./gradlew assembleRelease
   ```

2. **Create App Bundle for Play Store**
   ```bash
   ./gradlew bundleRelease
   ```

3. **Run ProGuard Optimization**
   - Release builds automatically apply ProGuard rules
   - See `proguard-rules.pro` for configuration

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Run UI Tests
```bash
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.afristyle.ai.ui.tests.UITestSuite
```

## Cultural Sensitivity

This app is designed with deep respect for African cultures and traditions. The fashion items included are:
- Researched for cultural accuracy
- Presented with proper historical context
- Sourced from authentic cultural references
- Designed to educate and celebrate African heritage

## Contributing

We welcome contributions that:
- Add more authentic African fashion styles
- Improve AI processing accuracy
- Enhance cultural education content
- Fix bugs or improve performance

Please ensure all contributions maintain cultural sensitivity and accuracy.

## Privacy & Data

- **Local Storage Only**: All user photos and data remain on the device
- **No Cloud Upload**: No automatic uploading of personal images
- **Offline Operation**: Full functionality without internet after initial setup
- **Data Control**: Users have complete control over their data

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- African fashion designers and cultural experts who provided guidance
- Open source libraries that made this project possible
- The African diaspora community for cultural insights and feedback

## Support

For support, please:
1. Check the [Issues](https://github.com/yourusername/afristyle-ai/issues) page
2. Create a new issue with detailed description
3. Include device information and steps to reproduce

## Roadmap

- [ ] Add more African fashion styles (Mud cloth, Shweshwe, etc.)
- [ ] Implement AR try-on features
- [ ] Add social sharing features
- [ ] Support for multiple languages
- [ ] Integration with African fashion e-commerce platforms

---

**AfriStyle AI** - Celebrating African Fashion Through Technology 🌍👗✨