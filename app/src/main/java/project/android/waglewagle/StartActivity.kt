package project.android.waglewagle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //액션바 가리기
        var actionBar = supportActionBar
        actionBar?.hide()

        //스케줄 등록
        val backgrondExecutor : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val mainExecutor : Executor = ContextCompat.getMainExecutor(this)

        //스케줄 잡기(1초 동안 보여줌)
        backgrondExecutor.schedule({
            mainExecutor.execute{
                val intent = Intent(applicationContext, ForLoginActivity::class.java)
                startActivity(intent)
                finish() //스플래시 화면 종료
            }
        }, 1, TimeUnit.SECONDS)
    }
}