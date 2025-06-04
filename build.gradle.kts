// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Firebase 설정 파일 (google-services.json)을 자동으로 앱에 적용하기 위한 플러그인
    alias(libs.plugins.google.services) apply false
}
// 프로젝트 전체에서 사용 가능한 플러그인 선언
// apply false : 아직 적용은 하지 않고 나중에 모듈에서 선택적으로 사용