package com.example.project

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.primera.content.subCategoriesClass


class card_subcategories_adapter(
    private val lisSubCategories: MutableList<subCategoriesClass>, private val types: String, private val context: Context, private val categorie: String,
) : RecyclerView.Adapter<card_subcategories_adapter.ViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cards = lisSubCategories[position]
        holder.img.visibility = View.VISIBLE
        Glide.with(holder.itemView.context).load(cards.url).into(holder.img);
        holder.txtTitulo.text = cards.title

        holder.cardActive.setOnClickListener {
            val intent = Intent( context, allContent::class.java).apply {
                putExtra("Type", cards.category)
                putExtra("subType", cards.id)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

        }

    }


    override fun getItemCount() = lisSubCategories.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardActive: CardView = itemView.findViewById(R.id.cardTop)
        val img: ImageView = itemView.findViewById(R.id.img)
        val txtTitulo: TextView = itemView.findViewById(R.id.ct)
        val txtSub: TextView = itemView.findViewById(R.id.ct2)

    }
}