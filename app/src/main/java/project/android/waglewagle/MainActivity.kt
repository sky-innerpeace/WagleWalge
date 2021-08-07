package project.android.waglewagle

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_write.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter:ItemAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "와글와글"

        //액션바 가리기
        var actionBar = supportActionBar
        actionBar?.hide()

        //StartActivity에서 보냄
        var id = intent.getStringExtra("email")
        var nickname = intent.getStringExtra("nickname")

        //레이아웃 연결
        val layoutManager = LinearLayoutManager(this)
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)    //역순 출력
        recyclerView.layoutManager = layoutManager
        //어댑터 초기화
        adapter = ItemAdapter("key", "아이디","별명", "이미지", "상품명입니다", "상품정보래요", "폰번호", "마감기한")

        recyclerView.adapter = adapter

        //db
        databaseRef = FirebaseDatabase.getInstance().reference

        databaseRef.orderByKey().limitToFirst(5).addValueEventListener(object:ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e("test", "loadItem:OnCancelled:${error.toException()}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                //데이터베이스의 변화를 감지해서 알아서 바꿔주는
                //snapshot은 접근
                loadItemList(snapshot)
            }
        })

        writeButton.setOnClickListener {
            var intent = Intent(this@MainActivity, WriteActivity::class.java)
            intent.putExtra("email", id)   //id
            intent.putExtra("nickname", nickname)  // 닉네임
            startActivityForResult(intent, 101)
        }

        //로그아웃 버튼
        logoutButton.setOnClickListener {
            Toast.makeText(applicationContext, "정상적으로 로그아웃되었습니다", Toast.LENGTH_SHORT).show()

            // 로그아웃하면 ForLoginActivity로 이동
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i("logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    intent = Intent(this@MainActivity, ForLoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            101 -> {
                if(resultCode== Activity.RESULT_OK) {
                    // 돌아오면 새로 쓴 글이 위로 스크롤해야 보일 수 있는 문제 발생
                    // 스크롤을 최상단으로 조정
                    recyclerView.smoothScrollToPosition(adapter.itemCount-1)
                }
            }
        }
    }

    fun loadItemList(dataSnapshot: DataSnapshot) {
        //items 내려옴
        val collectionIterator = dataSnapshot!!.children.iterator()
        if(collectionIterator.hasNext()) {
            adapter.items.clear()
            val items = collectionIterator.next()
            val itemsIterator = items.children.iterator()
            while (itemsIterator.hasNext()) {
                val currentItem = itemsIterator.next()
                val map = currentItem.value as HashMap<String, Any>
                val objectId = map["objectId"].toString()
                val id = map["id"].toString()
                val nickname = map["nickname"].toString()
                //이미지
                val uri = map["poster"].toString()
                val product_name = map["product_name"].toString()
                val product_info = map["product_info"].toString()
                val phone_number = map["phone_number"].toString()
                val time = map["time"].toString()

                adapter.items.add(Items(objectId, id, nickname, uri, product_name, product_info, phone_number, time))
            }
            adapter.notifyDataSetChanged()
        }
    }

}