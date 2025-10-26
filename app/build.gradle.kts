plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hotel_booking"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hotel_booking"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // RecyclerView and CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ✅ Thêm Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.room.common.jvm)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Nếu bạn dùng LiveData hoặc coroutines (phần mở rộng Room KTX)
    implementation("androidx.room:room-ktx:2.6.1")

    // Unit test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
