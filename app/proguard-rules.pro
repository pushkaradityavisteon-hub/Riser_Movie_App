# ──────────────────────────────────────────────────────────────
# Stack trace readability — keep line numbers in crash reports
# ──────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ──────────────────────────────────────────────────────────────
# Retrofit — keep API interfaces and their annotations
# ──────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes *Annotation*
-keep interface com.example.movie_app.data.remote.** { *; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# ──────────────────────────────────────────────────────────────
# Gson — keep network DTO fields so JSON parsing doesn't break
# @SerializedName already protects field names but keeping the
# classes ensures Gson can reflectively access them
# ──────────────────────────────────────────────────────────────
-keep class com.example.movie_app.data.remote.** { *; }
-dontwarn com.google.gson.**

# ──────────────────────────────────────────────────────────────
# Room — keep Entity and DAO classes
# Room generates code at compile time but entity field names
# must survive obfuscation for the schema to stay consistent
# ──────────────────────────────────────────────────────────────
-keep class com.example.movie_app.data.local.** { *; }
-dontwarn androidx.room.**

# ──────────────────────────────────────────────────────────────
# Domain model — keep pure Kotlin model class
# ──────────────────────────────────────────────────────────────
-keep class com.example.movie_app.domain.model.** { *; }

# ──────────────────────────────────────────────────────────────
# Hilt — generated classes must not be removed or renamed
# Hilt ships its own rules via AAR but this is a safety net
# ──────────────────────────────────────────────────────────────
-dontwarn dagger.**
-dontwarn javax.inject.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# ──────────────────────────────────────────────────────────────
# OkHttp + logging interceptor
# ──────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# ──────────────────────────────────────────────────────────────
# Kotlin coroutines
# ──────────────────────────────────────────────────────────────
-dontwarn kotlinx.coroutines.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ──────────────────────────────────────────────────────────────
# Parcelable — CREATOR field must not be removed
# ──────────────────────────────────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ──────────────────────────────────────────────────────────────
# Enums — values() and valueOf() used via reflection
# ──────────────────────────────────────────────────────────────
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ──────────────────────────────────────────────────────────────
# Glide
# ──────────────────────────────────────────────────────────────
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# ──────────────────────────────────────────────────────────────
# Jetpack Compose — keep composable function names for tooling
# ──────────────────────────────────────────────────────────────
-dontwarn androidx.compose.**
