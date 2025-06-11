package com.example.memorynote2025.ui.activity

import android.credentials.GetCredentialException
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.memorynote2025.R
import com.example.memorynote2025.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import com.example.memorynote2025.BuildConfig

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth // Firebase 인증
    private lateinit var credentialManager: CredentialManager // 자격 증명 관리자

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        credentialManager = CredentialManager.create(baseContext)

        initView()
    }

    private fun initView() {
        binding.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = getString(R.string.login)
            }
            toolbar.setNavigationOnClickListener {
                finish()
            }
            btnGoogleSignIn.setOnClickListener {
                launchCredentialManager()
            }
        }
    }

    // Credential Manager UI를 띄워서 사용자에게 구글 계정 선택 요청
    private fun launchCredentialManager() {
        val webClientId = BuildConfig.WEB_CLIENT_ID
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId) // 서버용 클라이언트 id 설정
            .setFilterByAuthorizedAccounts(false) // 모든 구글 계정 표시
            .build()

        // Credential Manager 요청 생성
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                try { // Credential Manager UI 실행
                    val result = credentialManager.getCredential(
                        context = baseContext,
                        request = request
                    )
                    handleSignIn(result.credential) // 사용자가 계정을 선택하면 호출
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
                }
            }
        }
    }

    // Credential 객체를 받아 구글 id 토큰인지 확인하고 추출
    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            // 구글 id 토큰 생성
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    // 해당 id 토큰으로 Firebase 로그인
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) { // 로그인 성공
                    Log.d(TAG, "signInWithCredential:success")
                    updateUI(auth.currentUser)
                } else { // 로그인 실패
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }
}