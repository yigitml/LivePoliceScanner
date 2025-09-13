# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp (make sure OkHttp is preserved)
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson (for deserialization)
-keep class com.google.gson.** { *; }
-keep class com.oakssoftware.livepolicescanner.** { *; }

# Gson reflection
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Hilt - Keep all Hilt classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class dagger.** { *; }

# Hilt components (e.g., WorkerFactory, ViewModels)
-keep class androidx.hilt.work.HiltWorkerFactory { *; }
-keep class androidx.hilt.work.HiltWorker { *; }

# Room Database
-keep class androidx.room.** { *; }
-keep class com.oakssoftware.livepolicescanner.data.room.StationDatabase.** { *; }
-keepclasseswithmembers class * {
    @androidx.room.* <methods>;
}

# Google Ads SDK
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }
-dontwarn com.google.android.gms.ads.**