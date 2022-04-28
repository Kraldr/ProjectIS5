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
import com.example.primera.menu.cardStart


class card_categories_adapter(
    private val card: MutableList<cardStart>, private val types: String, private val context: Context,
    private val lisSubCategories: MutableList<subCategoriesClass>,
) : RecyclerView.Adapter<card_categories_adapter.ViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cards = card[position]
        holder.img.visibility = View.VISIBLE
        Glide.with(holder.itemView.context).load(cards.urli).into(holder.img);
        holder.txtTitulo.text = cards.title

        var subCa = ""
        for (i in lisSubCategories){
            if (i.category == cards.id) {
                subCa = subCa + i.title + ","
            }
        }

        var count = 0
        for (i in 0..subCa.length) {
            if (i >= 44) {
                count++
            }
        }

        subCa = subCa.substring(0, subCa.length - count)

        holder.txtSub.text = "$subCa..."

        holder.cardActive.setOnClickListener {
            val intent = Intent( context, content::class.java).apply {
                putExtra("Type", cards.id)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }


    override fun getItemCount() = card.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardActive: CardView = itemView.findViewById(R.id.cardTop)
        val img: ImageView = itemView.findViewById(R.id.img)
        val txtTitulo: TextView = itemView.findViewById(R.id.ct)
        val txtSub: TextView = itemView.findViewById(R.id.ct2)

    }
}