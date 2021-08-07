package project.android.waglewagle

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {

        super.onCreate()

        // 카카오 sdk 초기화
        KakaoSdk.init(this, "e2609deb8d0c0eef8cf7bfb40f5b22ae")
    }
}