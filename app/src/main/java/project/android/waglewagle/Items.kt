package project.android.waglewagle

import com.google.firebase.database.Exclude

data class Items (
    var objectId : String,
    var id : String,
    var nickname : String,
    var poster : String,
    var product_name : String,
    var product_info : String,
    var phone_number : String,
    var time : String
) {
    @Exclude
    fun toMap() : HashMap<String, Any> {
        val result : HashMap<String, Any> = HashMap()
        result["objectId"] = objectId
        result["id"] = id
        result["nickname"] = nickname
        result["poster"] = poster
        result["product_name"] = product_name
        result["product_info"] = product_info
        result["phone_number"] = phone_number
        result["time"]= time
        return result
    }
}