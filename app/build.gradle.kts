plugins { // 이 모듈(app)에 실제로 필요한 기능을 플러그인으로 적용
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.google.services) // Firebase 연동
}

android {
    namespace = "com.example.memorynote2025"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.memorynote2025"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true // 구버전 Android에서 최신 Java 기능 사용 가능
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room
    implementation(libs.androidx.room.runtime) // 기본 기능
    ksp(libs.androidx.room.compiler) // DAO 구현체 자동 생성 (KSP로 최적화)
    implementation(libs.androidx.room.ktx) // 코틀린 확장 기능 (코루틴, Flow)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Fragment API에 코틀린 확장 기능(KTX) 추가
    implementation(libs.androidx.fragment.ktx)

    // Preference
    implementation(libs.androidx.preference.ktx)

    implementation(libs.play.services.base)
    coreLibraryDesugaring(libs.corelibrarydesugaring)

    implementation(libs.firebase.auth) // Firebase 인증
    implementation(libs.play.services.auth) // 구글 로그인
}