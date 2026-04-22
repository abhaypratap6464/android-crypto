# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── kotlinx.serialization ──────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.abhay.crypto.**$$serializer { *; }
-keepclassmembers class com.abhay.crypto.** {
    *** Companion;
}
-keepclasseswithmembers class com.abhay.crypto.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Domain models (serialized to/from DataStore JSON) ─────────────────────
-keep class com.abhay.crypto.domain.model.** { *; }

# ── Retrofit + OkHttp ─────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keepattributes Signature, Exceptions

# ── Hilt / Dagger ─────────────────────────────────────────────────────────
-dontwarn com.google.dagger.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ── Glance widget ─────────────────────────────────────────────────────────
-keep class androidx.glance.** { *; }
-keep class com.abhay.crypto.presentation.glance.** { *; }
