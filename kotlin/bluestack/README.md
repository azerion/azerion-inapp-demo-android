# BlueStack Android SDK Demo (Kotlin)

An Android demo app showcasing ad integration and key features of the BlueStack Android SDK using Kotlin and modern Android development practices.

## Features

- Navigation drawer with multiple ad format screens
- BlueStack SDK integration (v5.3.3)
- Banner, MREC, Interstitial, and Rewarded ads support
- ViewBinding for type-safe view access
- Navigation Component for seamless navigation
- Splash screen with SDK initialization
- Content feed with in-stream banner ads
- Multiple banner ad size configurations

## Getting Started

### Prerequisites

- Android Studio (Arctic Fox or later recommended)
- JDK 17 or higher
- Android SDK with minimum API level 23 (Android 6.0)
- A physical device or emulator running Android 6.0 or higher

### Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Update your BlueStack credentials in [`Constants.kt`](app/src/main/java/com/azerion/bluestack/demo/kotlin/Constants.kt):
   ```kotlin
   const val APP_ID = "YOUR_APP_ID"
   const val BANNER_PLACEMENT_ID = "YOUR_BANNER_PLACEMENT"
   const val MREC_PLACEMENT_ID = "YOUR_MREC_PLACEMENT"
   const val INTERSTITIAL_PLACEMENT_ID = "YOUR_INTERSTITIAL_PLACEMENT"
   const val REWARDED_VIDEO_PLACEMENT_ID = "YOUR_REWARDED_PLACEMENT"
   ```
5. Run the app on your device or emulator

### Build and Run

Using Gradle:
```bash
# Build debug APK
./gradlew assembleDebug

# Install and run on connected device
./gradlew installDebug

# Run app
adb shell am start -n com.azerion.bluestack.demo.kotlin/.SplashScreenActivity
```

Using Android Studio:
- Click the "Run" button or press `Shift + F10`

## Project Structure

```
app/src/main/
├── AndroidManifest.xml              # App configuration and permissions
├── java/com/azerion/bluestack/demo/kotlin/
│   ├── Constants.kt                 # App ID and placement configurations
│   ├── MainActivity.kt              # Main activity with navigation drawer
│   ├── SplashScreenActivity.kt      # Splash screen with SDK initialization
│   ├── CustomLauncherActivity.kt    # Alternative custom splash screen
│   ├── InterstitialFragment.kt      # Interstitial ad implementation
│   ├── RewardedAdFragment.kt        # Rewarded ad implementation
│   ├── Logger.kt                    # Logging utility
│   ├── DimensionUtils.kt            # UI dimension helpers
│   ├── DummyCMPManager.kt           # Consent Management Platform example
│   ├── Extensions.kt                # Kotlin extension functions
│   └── banner/
│       ├── BannerAdFragment.kt      # Banner ad with content feed
│       ├── BannerAdTabFragment.kt   # Tab-based banner examples
│       ├── BannerAdTabPagerAdapter.kt # ViewPager adapter for tabs
│       ├── MrecFragment.kt          # MREC ad implementation
│       ├── BannerAdManager.kt       # Banner ad lifecycle management
│       ├── BannerAdListener.kt      # Ad event callbacks
│       ├── BannerAdsAdapter.kt      # RecyclerView adapter with ads
│       ├── BannerAdSizeConfig.kt    # Ad size configurations
│       ├── BannerState.kt           # Banner state management
│       └── ContentItem.kt           # Content model for feed
└── res/
    ├── layout/                      # XML layouts
    ├── drawable/                    # Images and icons
    ├── menu/                        # Navigation menu
    ├── navigation/                  # Navigation graph
    └── values/                      # Strings, colors, themes
```

## SDK Integration

### Dependencies

The app uses the BlueStack SDK via Gradle:

```kotlin
dependencies {
    implementation("com.azerion:bluestack-sdk-core:5.3.3")
    // Other dependencies...
}
```

### Initialization

The SDK is initialized in the [`SplashScreenActivity`](app/src/main/java/com/azerion/bluestack/demo/kotlin/SplashScreenActivity.kt) before the main app launches:

```kotlin
BlueStackSDK.initialize(context, APP_ID)
```

### Ad Formats

#### Banner Ads
- Standard banner sizes (320x50, 320x100, 728x90)
- Integrated into content feeds using [`BannerAdsAdapter`](app/src/main/java/com/azerion/bluestack/demo/kotlin/banner/BannerAdsAdapter.kt)
- Tab-based examples in [`BannerAdTabFragment`](app/src/main/java/com/azerion/bluestack/demo/kotlin/banner/BannerAdTabFragment.kt)

#### MREC Ads
- Medium Rectangle (300x250) ads
- Implemented in [`MrecFragment`](app/src/main/java/com/azerion/bluestack/demo/kotlin/banner/MrecFragment.kt)

#### Interstitial Ads
- Full-screen ads with load and show logic
- Implemented in [`InterstitialFragment`](app/src/main/java/com/azerion/bluestack/demo/kotlin/InterstitialFragment.kt)

#### Rewarded Ads
- Video ads with reward callbacks
- Implemented in [`RewardedAdFragment`](app/src/main/java/com/azerion/bluestack/demo/kotlin/RewardedAdFragment.kt)

## Architecture

### Navigation
The app uses Android Navigation Component with a navigation drawer:
- Navigation graph: [`mobile_navigation.xml`](app/src/main/res/navigation/mobile_navigation.xml)
- Navigation menu: [`activity_main_drawer.xml`](app/src/main/res/menu/activity_main_drawer.xml)
- Main navigation: [`MainActivity`](app/src/main/java/com/azerion/bluestack/demo/kotlin/MainActivity.kt)

### View Binding
ViewBinding is enabled for type-safe view access:
```kotlin
buildFeatures {
    viewBinding = true
}
```

### Splash Screen

The demo app provides **two splash screen implementation options** for SDK initialization:

#### Option 1: System-Integrated Splash Screen (Default)
Uses Android 12+ Splash Screen API for a native, system-integrated experience.

**Implementation**: [`SplashScreenActivity`](app/src/main/java/com/azerion/bluestack/demo/kotlin/SplashScreenActivity.kt)

```kotlin
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isSDKInitialized }
        super.onCreate(savedInstanceState)
        // Initialize CMP and SDK...
    }
}
```

**Features**:
- Native Android 12+ splash screen experience
- Automatically handles splash dismissal when SDK is ready
- Backwards compatible with older Android versions via AndroidX library
- Clean, minimal setup

#### Option 2: Custom Splash Screen Layout
Provides full control over splash screen appearance with custom layout.

**Implementation**: [`CustomLauncherActivity`](app/src/main/java/com/azerion/bluestack/demo/kotlin/CustomLauncherActivity.kt)

```kotlin
class CustomLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize CMP and SDK...
    }
}
```

**Features**:
- Custom layout with full design control
- Can include branding, animations, or custom UI elements
- Works on all Android versions (API 23+)

#### Switching Between Splash Screen Options

To switch between splash screen implementations, modify [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml):

**For System Splash (Current Default)**:
```xml
<!-- Option 1: System-integrated splash (currently active) -->
<activity
    android:name=".SplashScreenActivity"
    android:exported="true"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.BlueStackDemo.SplashScreenAPI">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**For Custom Splash**:
Comment out the System Splash activity above and uncomment:
```xml
<!-- Option 2: Custom splash layout (uncomment to activate) -->
<activity
    android:name=".CustomLauncherActivity"
    android:exported="true"
    android:screenOrientation="portrait"
    android:theme="@style/Theme.BlueStackDemo.Splash">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

**Note**: The demo uses `android:screenOrientation="portrait"` for consistent UX. For production apps, consider using `"unspecified"`, `"fullSensor"`, or whatever orientation works best for your app.

#### SDK Initialization Flow

Both splash screen implementations follow the same initialization flow:

1. **CMP Integration**: Initialize Consent Management Platform using [`DummyCMPManager`](app/src/main/java/com/azerion/bluestack/demo/kotlin/DummyCMPManager.kt)
2. **SDK Initialization**: Call `BlueStack.initialize()` with your App ID
3. **Adapter Status**: Receive initialization status for all ad network adapters
4. **Navigation**: Automatically navigate to [`MainActivity`](app/src/main/java/com/azerion/bluestack/demo/kotlin/MainActivity.kt) when complete
5. **Cleanup**: Clear the splash activity from the back stack

## Permissions

The app requests optional location permissions to improve ad targeting:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Configuration

### Build Configuration
- **Namespace**: `com.azerion.bluestack.demo.kotlin`
- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 17
- **Kotlin JVM Target**: 17

## Development

### Logging
Use the [`Logger`](app/src/main/java/com/azerion/bluestack/demo/kotlin/Logger.kt) utility for consistent logging throughout the app.

## Testing

Run the app on different devices to test:
- Different screen sizes and orientations
- Various Android versions (6.0+)
- Different ad formats and placements
- Ad loading and error states

## Troubleshooting

- **Ads not loading**: Verify your App ID and placement IDs in [`Constants.kt`](app/src/main/java/com/azerion/bluestack/demo/kotlin/Constants.kt)
- **Build errors**: Ensure you have JDK 17 and the latest Android SDK components
- **Navigation issues**: Check that the Navigation Component dependencies are properly synced

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues related to the BlueStack SDK, please contact Azerion support or visit the [BlueStack SDK documentation](https://developers.bluestack.app/).
