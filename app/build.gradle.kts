import java.io.FileInputStream
import java.util.Properties

plugins { // 모듈(app)에 실제로 필요한 기능을 플러그인으로 적용
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    alias(libs.plugins.google.services) // Firebase 연동을 위한 구글 서비스
}

val properties = Properties() // 설정 파일 속성 읽기
// 프로젝트 루트 디렉토리의 local.properties를 읽음
properties.load(FileInputStream(rootProject.file("local.properties")))
// local.properties에서 해당 값을 가져오고, 없으면 빈 문자열 반환
val webClientId = properties.getProperty("WEB_CLIENT_ID") ?: ""

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

        // BuildConfig에 상수를 문자열로 추가
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
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
        buildConfig = true
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

    implementation(platform(libs.firebase.bom)) // Firebase 버전 통합 관리
    implementation(libs.firebase.auth) // Firebase 인증
    implementation(libs.credentials) // 자격 증명 관리자 기본 라이브러리
    implementation(libs.credentials.play.services.auth) // 구글 로그인을 위한 Play Services 연동
    implementation(libs.googleid) // 구글 id 로그인
}