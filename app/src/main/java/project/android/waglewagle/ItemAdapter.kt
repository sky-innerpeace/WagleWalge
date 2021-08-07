package project.android.waglewagle

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_write.view.*
import kotlinx.android.synthetic.main.items.view.*
import kotlinx.android.synthetic.main.items.view.product_info
import kotlinx.android.synthetic.main.items.view.product_name


class ItemAdapter(
    objectId: String,
    id: String,
    nickname: String,
    poster: String,
    product_name: String,
    product_info: String,
    phone_number: String,
    time: String
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    var items = ArrayList<Items>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun setItem(item: Items) {

            Glide.with(itemView).load(item.poster).into(itemView.poster)
            itemView.product_name.text = item.product_name
            itemView.product_info.text = item.product_info
            itemView.nickname.text = item.nickname
            itemView.time.text = item.time

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ItemActivity::class.java)
                //필요한 거 하나씩 보내줌 to ItemActivity(세부화면)
                intent.putExtra("product_name", item.product_name)
                intent.putExtra("product_info", item.product_info)
                intent.putExtra("nickname", item.nickname)
                intent.putExtra("phone_number", item.phone_number)
                intent.putExtra("poster", item.poster)
                Log.e("poster",item.poster)
                startActivity(itemView.context, intent, null)
            }
        }



    }
}