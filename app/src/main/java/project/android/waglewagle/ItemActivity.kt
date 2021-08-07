package project.android.waglewagle

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.activity_item.product_info
import kotlinx.android.synthetic.main.activity_item.product_name
import kotlinx.android.synthetic.main.activity_item.phone_number

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        //intent 받음
        var pro_name = intent.getStringExtra("product_name")
        var pro_info = intent.getStringExtra("product_info")
        var name = intent.getStringExtra("nickname")
        var number = intent.getStringExtra("phone_number")
        var p = intent.getStringExtra("poster")

        //사진 표시
        Picasso.get()
            .load(Uri.parse(p))
            .resize(500,500)
            .into(img)
        nickname.text = name
        product_info.text = pro_info
        product_name.text = pro_name
        // 등록된 전화번호로 전화 걸기
        phone_number.setOnClickListener {
            val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:/"+number))
            startActivity(mIntent)
        }
    }
}