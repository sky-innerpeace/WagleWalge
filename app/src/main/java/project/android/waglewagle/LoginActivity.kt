package project.android.waglewagle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    var TAG = "kakaologin"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = "와글와글"


        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        var scopes = mutableListOf<String>()
                        // 이메일 정보 받아오기
                        if (user.kakaoAccount?.email != null) {
                            Log.e("KAKAO_API", "email: ${user.kakaoAccount?.email.toString()}")
                        } else if (user.kakaoAccount?.emailNeedsAgreement == true) {
                            // 동의 요청 후 이메일 획득 가능
                            scopes.add("account_email")

                            // 동의 요청 후 프로필 정보 획득 가능
                            if (user.kakaoAccount?.profileNeedsAgreement == true)
                                scopes.add("profile")

                            UserApiClient.instance.loginWithNewScopes(applicationContext, scopes) { token, error ->
                                if (error != null) {
                                    Log.e(TAG, "사용자 추가 동의 실패", error)
                                } else {
                                    UserApiClient.instance.me { user, error ->
                                        if (error != null) {
                                            Log.e(TAG, "사용자 정보 요청 실패", error)
                                        } else if (user != null) {
                                            Log.i(TAG, "사용자 정보 요청 성공 : ${user.kakaoAccount?.email} ${user.kakaoAccount?.profile}")
                                        }
                                    }
                                }
                            }
                        } else {
                            // 이메일 획득 불가
                            Toast.makeText(applicationContext, "이메일 획득 불가", Toast.LENGTH_SHORT).show()
                        }
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("email", user.kakaoAccount?.email.toString())   //이메일 -> id로 쓸 거임
                        intent.putExtra("nickname", user.kakaoAccount?.profile?.nickname.toString())  // 닉네임
                        intent.putExtra("prf_img", user.kakaoAccount?.profile?.thumbnailImageUrl.toString())  //url형태의 프로필 이미지
                        startActivity(intent)
                        finish()
                    }
                }
                var i = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(i)
                finish()

            }
        }

// 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(applicationContext)) {
            UserApiClient.instance.loginWithKakaoTalk(applicationContext, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(applicationContext, callback = callback)
        }
    }
}
