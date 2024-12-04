package edu.sswu.petopia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

data class Restaurant(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val menu: String,
    val hours: String,
    val contact: String,
    val address: String,
    val address22: String,
    val category: String,
    val imageUrl: String
)

class RestaurantAdapter(
    private var items: List<Restaurant>,
    private val onClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.restaurant_image)
        val name: TextView = view.findViewById(R.id.restaurant_name)
        val location: TextView = view.findViewById(R.id.restaurant_location)
        val category: TextView = view.findViewById(R.id.restaurant_category)
        val menu: TextView = view.findViewById(R.id.restaurant_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.location.text = item.location
        holder.menu.text = item.menu
        holder.category.text = item.category

        // 이미지 URL 확인 후 Coil로 이미지 로드
        val imageToLoad = if (item.imageUrl.isEmpty()) {
            R.drawable.sample_image // 기본 이미지 리소스 ID
        } else {
            item.imageUrl // 실제 이미지 URL
        }

        holder.image.load(imageToLoad) {
            placeholder(R.drawable.placeholder_image) // 로딩 중 표시할 이미지
            error(R.drawable.error_image) // 에러 발생 시 표시할 이미지
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }


    override fun getItemCount(): Int = items.size

    // **새로운 데이터로 업데이트**
    fun updateData(newItems: List<Restaurant>) {
        items = newItems
        notifyDataSetChanged() // RecyclerView를 새 데이터로 갱신
    }
}
