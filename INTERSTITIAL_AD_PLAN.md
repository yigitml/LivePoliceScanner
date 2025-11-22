# Implementation Plan: One-Time Interstitial Ad

## Overview
Implement a one-time interstitial ad that displays **after** the user lands on `StationDetailScreen` for the first time. The ad will show only once per app installation and will be skipped on subsequent navigations.

## AdMob Policy Compliance ✅

Based on current AdMob policies (2024), showing an interstitial ad **after** the user lands on a screen and sees content first is **fully compliant** and considered **best practice**:

- ✅ Shows ads at natural transition points
- ✅ Prioritizes user experience and content first  
- ✅ Avoids surprising users with immediate ads
- ✅ Prevents accidental clicks
- ✅ Does not obstruct app functionality

**Source**: AdMob guidelines emphasize that ads should be shown between content at natural breaks, never before the main screen is displayed, and content should always take precedence over ads.

---

## Proposed Changes

### 1. Create AdPreferences Utility

**File**: `app/src/main/java/com/oakssoftware/livepolicescanner/util/AdPreferences.kt` [NEW]

Purpose: Track whether the interstitial ad has been shown using SharedPreferences

```kotlin
@Singleton
class AdPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("ad_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_STATION_DETAIL_INTERSTITIAL_SHOWN = "station_detail_interstitial_shown"
    }
    
    fun hasShownStationDetailInterstitial(): Boolean {
        return prefs.getBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, false)
    }
    
    fun setStationDetailInterstitialShown() {
        prefs.edit().putBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, true).apply()
    }
    
    // For testing purposes only
    fun resetStationDetailInterstitial() {
        prefs.edit().putBoolean(KEY_STATION_DETAIL_INTERSTITIAL_SHOWN, false).apply()
    }
}
```

**Dependencies**: Uses Hilt `@Singleton` and `@Inject` annotations (already in project)

---

### 2. Create Interstitial Ad Manager

**File**: `app/src/main/java/com/oakssoftware/livepolicescanner/ui/components/InterstitialAdManager.kt` [NEW]

Purpose: Composable helper to load and display interstitial ads

```kotlin
@Composable
fun ShowInterstitialAd(
    activity: Activity?,
    adUnitId: String,
    shouldShow: Boolean,
    onAdDismissed: () -> Unit,
    onAdFailed: () -> Unit
) {
    // Load ad when shouldShow becomes true
    // Show ad when loaded
    // Call onAdDismissed when ad is dismissed or closed
    // Call onAdFailed if ad fails to load
}
```

**Key Features**:
- Loads ad asynchronously using Google Mobile Ads SDK
- Handles all ad lifecycle callbacks
- Gracefully handles failures (calls onAdFailed immediately)
- Prevents multiple ad loads with state management

---

### 3. Update Constants

**File**: `app/src/main/java/com/oakssoftware/livepolicescanner/util/Constants.kt` [ALREADY DONE]

User has already added the interstitial ad unit ID constant.

---

### 4. Update StationDetailScreen

**File**: `app/src/main/java/com/oakssoftware/livepolicescanner/ui/screens/station_detail/view/StationDetailScreen.kt` [MODIFY]

**Changes**:
1. Inject `AdPreferences` via Hilt
2. Add state to track if ad should be shown
3. Use `LaunchedEffect` to show ad after screen composition
4. Show ad after a brief delay (500ms) to let user see the screen first

**Implementation approach**:
```kotlin
@Composable
fun StationDetailScreen(
    ip: PaddingValues, 
    viewModel: StationDetailViewModel = hiltViewModel(),
    adPreferences: AdPreferences = hiltViewModel<AdPreferencesHolder>().adPreferences
) {
    val activity = LocalActivity.current
    var shouldShowAd by remember { mutableStateOf(false) }
    
    // Check if we should show the ad after a brief delay
    LaunchedEffect(Unit) {
        delay(500) // Let user see the screen first
        if (!adPreferences.hasShownStationDetailInterstitial()) {
            shouldShowAd = true
        }
    }
    
    // Show interstitial ad
    if (shouldShowAd) {
        ShowInterstitialAd(
            activity = activity,
            adUnitId = Constants.INTERSTITIAL_STATION_DETAIL,
            shouldShow = shouldShowAd,
            onAdDismissed = {
                adPreferences.setStationDetailInterstitialShown()
                shouldShowAd = false
            },
            onAdFailed = {
                adPreferences.setStationDetailInterstitialShown()
                shouldShowAd = false
            }
        )
    }
    
    // Rest of existing UI code...
}
```

**Note**: We'll need to create a simple ViewModel holder for AdPreferences or inject it differently.

---

### 5. Alternative: Inject AdPreferences via ViewModel

**File**: `app/src/main/java/com/oakssoftware/livepolicescanner/ui/screens/station_detail/StationDetailViewModel.kt` [MODIFY]

**Simpler approach**: Inject `AdPreferences` into the existing `StationDetailViewModel`

```kotlin
@HiltViewModel
class StationDetailViewModel @Inject constructor(
    // ... existing dependencies
    private val adPreferences: AdPreferences
) : ViewModel() {
    
    fun shouldShowInterstitialAd(): Boolean {
        return !adPreferences.hasShownStationDetailInterstitial()
    }
    
    fun markInterstitialAdShown() {
        adPreferences.setStationDetailInterstitialShown()
    }
}
```

This is cleaner and doesn't require creating a separate ViewModel holder.

---

## Implementation Steps

1. ✅ Research AdMob policy compliance
2. Create `AdPreferences.kt` utility class
3. Create `InterstitialAdManager.kt` composable
4. Inject `AdPreferences` into `StationDetailViewModel`
5. Update `StationDetailScreen` to show ad after landing
6. Test the implementation

---

## Verification Plan

### Manual Testing (Primary Method)

**Test 1: First-Time Navigation**
1. Clear app data: Settings → Apps → Live Police Scanner → Storage → Clear Data
2. Launch the app
3. Navigate to Stations screen
4. Click on any station
5. **Expected**: User lands on Station Detail screen and sees content
6. **Expected**: After ~500ms, interstitial ad appears
7. Close the ad
8. **Expected**: User remains on Station Detail screen

**Test 2: Subsequent Navigations**
1. Navigate back to Stations screen
2. Click on a different station
3. **Expected**: User lands on Station Detail screen immediately
4. **Expected**: No interstitial ad is shown
5. Navigate back and try multiple stations
6. **Expected**: No ads shown on any subsequent navigation

**Test 3: Ad Load Failure**
1. Clear app data
2. Enable airplane mode
3. Navigate to Stations → Click a station
4. **Expected**: User lands on Station Detail screen
5. **Expected**: No ad shows (fails to load silently)
6. **Expected**: User can interact with the screen normally

**Test 4: Persistence Across App Restarts**
1. After completing Test 1 (ad shown once)
2. Force close the app
3. Relaunch the app
4. Navigate to Stations → Click a station
5. **Expected**: No ad is shown (preference persists)

### Automated Testing (Optional)

Create a unit test for `AdPreferences`:

**File**: `app/src/test/java/com/oakssoftware/livepolicescanner/AdPreferencesTest.kt` [NEW]

```kotlin
class AdPreferencesTest {
    @Test
    fun testInitialState_shouldReturnFalse() {
        // Test that hasShownStationDetailInterstitial returns false initially
    }
    
    @Test
    fun testAfterSetting_shouldReturnTrue() {
        // Test that after calling setStationDetailInterstitialShown, 
        // hasShownStationDetailInterstitial returns true
    }
}
```

**Run command**: `./gradlew test`

---

## Questions for User

1. ✅ **Ad Unit ID**: Already added to Constants.kt
2. ✅ **Ad Timing**: Confirmed - show after user lands on screen
3. **Delay Duration**: Is 500ms delay appropriate, or would you prefer a different duration?
4. **Test Mode**: Should I keep using test ad unit ID initially for development?

---

## Risk Assessment

- **Low Risk**: Using established patterns (Hilt, SharedPreferences, Compose)
- **Ad Load Failures**: Handled gracefully - won't block user experience
- **Policy Compliance**: Fully compliant with AdMob guidelines
- **User Experience**: Minimal impact - ad shows only once, after content is visible
