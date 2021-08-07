package project.android.waglewagle

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.FileProvider
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_write.*
import java.io.File

class WriteActivity : AppCompatActivity() {

    var selectYear : Int = 0
    var selectMonth : Int = 0
    var selectDay : Int = 0
    var time : String = ""

    var uri : Uri? = null
    var imgUri = ""

    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        //MainActivity에서 보냄
        var id = intent.getStringExtra("email")
        var nickname = intent.getStringExtra("nickname")

        //캘린더뷰 연, 월, 일 가져와서 time string으로 저장
        // i = year i2 = month i3 = day
        calendarView.setOnDateChangeListener { calendarView, i, i2, i3 ->
            selectYear = i
            // month 는 시작이 0부터
            selectMonth = i2 + 1
            selectDay = i3

            time = selectYear.toString()+"년"+selectMonth.toString()+"월"+selectDay.toString()+"일"
        }

        //카메라 권한 허용
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted { permissions ->
                Log.d("Main", "허용된 권한 갯수 : ${permissions.size}")
            }
            .onDenied { permissions ->
                Log.d("Main", "거부된 권한 갯수 : ${permissions.size}")
            }.start()

        //사진찍기 버튼
        captureButton.setOnClickListener {
            takePhoto()
        }
        //갤러리 버튼
        galleryButton.setOnClickListener {
            getFromAlbum()
        }

        //사진 확정 버튼
        photosaveButton.setOnClickListener {
            //cloud storage에 저장
            val storage = Firebase.storage
            // Create a storage reference from our app
            val storageRef = storage.reference

            var file = Uri.fromFile(File("/storage/self/primary/Android/data/project.android.waglewagle/cache/captured.jpg"))
            val riversRef = storageRef.child("images/${file.lastPathSegment}")
            var uploadTask = riversRef.putFile(file)

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads

            }.addOnSuccessListener{ taskSnapshot ->
                // 사진 저장에 성공했을 경우 저장한 사진을 이용해 URL 생성
                storageRef.child("/storage/self/primary/Android/data/project.android.waglewagle/cache/captured.jpg").downloadUrl
                        .addOnSuccessListener { myuri ->
                            Log.e("url", myuri.toString())
                            imgUri = myuri.toString()

                        }.addOnFailureListener {
                            // Handle any errors
                            Log.e("url", "오류")
                        }
            }
            //사진을 확정하고 나면 사진 변경 불가
            captureButton.isClickable = false
            galleryButton.isClickable = false
        }

        //저장하면 데이터베이스로 가는 코드
        saveButton.setOnClickListener {
            val product_name = product_name.text.toString()
            val product_info = product_info.text.toString()
            val phone_number = phone_number.text.toString()

            databaseRef = FirebaseDatabase.getInstance().reference

            //초기화
            saveComment(id!!, nickname!!, imgUri, product_name, product_info, phone_number, time)
            intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            101 -> {
                if(resultCode== Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
                    output.setImageBitmap(bitmap)
                }
            }
            102 -> {
                if(resultCode== Activity.RESULT_OK) {
                    if(Build.VERSION.SDK_INT >=19){
                        uri = data?.data
                        if(uri != null){
                            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
                            output.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }
    }

    // 데이터베이스에 저장하는 문장
    fun saveComment(id:String, nickname:String, uri:String, product_name: String =" ", product_info: String=" ", phone_number: String, time: String) {


        val key: String? = databaseRef.child("items").push().getKey()
        val item =
            Items(key!!, id, nickname, imgUri, product_name, product_info, phone_number, time)

        val itemValues: HashMap<String, Any> = item.toMap()


        //파이어베이스에 저장
        val childUpdates: MutableMap<String, Any> = HashMap()
        childUpdates["/items/$key"] = itemValues
        databaseRef.updateChildren(childUpdates)


    }
    fun takePhoto() {
        val captureFile = File(externalCacheDir, "captured.jpg")
        if(captureFile.exists()) {
            captureFile.delete()
        }
        captureFile.createNewFile()
        uri = if(Build.VERSION.SDK_INT >= 24) {
            FileProvider.getUriForFile(this, "project.android.waglewagle.fileprovider", captureFile)
        }
        else {
            Uri.fromFile(captureFile)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, 101)
    }
    fun getFromAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, 102)
    }
}