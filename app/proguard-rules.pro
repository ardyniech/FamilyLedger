# FamilyLedger ProGuard Rules

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Moshi (JSON serialization)
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-dontwarn javax.annotation.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# FamilyLedger data models (keep for serialization)
-keep class com.example.core.storage.models.** { *; }
-keep class com.example.modules.updater.models.** { *; }
-keep class com.example.core.sync.models.** { *; }
-keep class com.example.modules.dashboard.csv.** { *; }

# Keep DataStore preferences
-keep class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# Keep EncryptedSharedPreferences
-keep class androidx.security.crypto.** { *; }

# Keep Compose classes
-dontwarn androidx.compose.**

# Suppress warnings for missing Firebase (used in dev without google-services.json)
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
