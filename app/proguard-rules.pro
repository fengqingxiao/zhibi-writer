# Add project specific ProGuard rules here.

# Keep data classes for Room
-keep class com.zhibi.writer.data.local.entity.** { *; }

# Keep serialization
-keepattributes *Annotation*, Signature
-keepclassmembers class **$serializer {
    *** Companion;
}
-keep class **$$serializer { *; }
-keep class kotlin.** { *; }
-keep class kotlinx.serialization.** { *; }

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
