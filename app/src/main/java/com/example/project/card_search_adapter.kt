package com.example.project

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.android.material.chip.Chip
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.database.FirebaseDatabase

class card_search_adapter(
    private val card: MutableList<contentClass>,
    private val context: Context?,
    private val type: String,
    private val listcard: MutableList<subCategoriesClass>,
    private val listCategory:MutableList<cardStart>,
    private val search: String,
) : RecyclerView.Adapter<card_search_adapter.ViewHolder> () {

    private var count: Int = 0
    private lateinit var dialogMenu: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_in, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cards = card[position]
        Glide.with(holder.itemView.context).load(cards.url).into(holder.img);
        holder.ct.text = cards.title
        holder.ct2.text = cards.descrip
        holder.chip3.text = cards.typeSelect
        for (i in listCategory) {
            if (i.id == cards.type) {
                holder.chip1.text = i.title
            }
        }
        for (i in listcard) {
            if (i.id == cards.typeSubTitle) {
                holder.chip2.text = i.title
            }
        }


        if (cards.typeSelect == "Video") {
            holder.img.visibility = View.INVISIBLE
            holder.videoV.visibility = View.VISIBLE
            if (type == "Organizador") {

                holder.cardTop.setOnClickListener {
                    dialogMenu = AlertDialog.Builder(context)
                        .setTitle("Ver, modifcar o eliminar contenido")
                        .setMessage("Seleccione una opción para gestionar su contenido")
                        .setNegativeButton("Editar") { view, _ ->
                            val intent = Intent( context, editContent::class.java).apply {
                                putExtra("key", cards.id)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context?.startActivity(intent)
                            view.dismiss()
                        }
                        .setPositiveButton("Ver") { view, _ ->
                            val intent = Intent( context, contentRepro::class.java).apply {
                                putExtra("url", cards.url)
                                putExtra("typeSelectedVideo", cards.typeSelectVideo)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context?.startActivity(intent)
                            view.dismiss()
                        }
                        .setNeutralButton("Eliminar") { view, _ ->

                            view.dismiss()
                            dialogMenu = AlertDialog.Builder(context)
                                .setTitle("Eliminar contenido")
                                .setMessage("Esta seguro de eliminar este contenido?")
                                .setNegativeButton("Cancelar") { view, _ ->
                                    Toast.makeText(context, "Se cancelo correctamente", Toast.LENGTH_SHORT).show()
                                    view.dismiss()
                                }
                                .setPositiveButton("Aceptar") { view, _ ->
                                    val database = FirebaseDatabase.getInstance().reference.child("content").child(cards.id);
                                    database.removeValue();
                                    Toast.makeText(context, "Contenido eliminado correctamente", Toast.LENGTH_SHORT).show()
                                    view.dismiss()
                                }
                                .setCancelable(false)
                                .create()

                            dialogMenu.show()

                        }
                        .setCancelable(false)
                        .create()

                    dialogMenu.show()
                }

            }else {
                holder.videoV.setOnClickListener {
                    val intent = Intent( context, contentRepro::class.java).apply {
                        putExtra("url", cards.url)
                        putExtra("typeSelectedVideo", cards.typeSelectVideo)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent)

                }

            }
        }else {
            if (type == "Organizador") {

                holder.cardTop.setOnClickListener {
                    dialogMenu = AlertDialog.Builder(context)
                        .setTitle("Ver, modifcar o eliminar contenido")
                        .setMessage("Seleccione una opción para gestionar su contenido")
                        .setPositiveButton("Editar") { view, _ ->
                            val intent = Intent( context, editContent::class.java).apply {
                                putExtra("key", cards.id)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context?.startActivity(intent)
                            view.dismiss()
                        }
                        .setNegativeButton("Eliminar") { view, _ ->
                            view.dismiss()
                            dialogMenu = AlertDialog.Builder(context)
                                .setTitle("Eliminar contenido")
                                .setMessage("Esta seguro de eliminar este contenido?")
                                .setNegativeButton("Cancelar") { view, _ ->
                                    Toast.makeText(context, "Se cancelo correctamente", Toast.LENGTH_SHORT).show()
                                    view.dismiss()
                                }
                                .setPositiveButton("Aceptar") { view, _ ->
                                    val database = FirebaseDatabase.getInstance().reference.child("content").child(cards.id);
                                    database.removeValue();
                                    Toast.makeText(context, "Contenido eliminado correctamente", Toast.LENGTH_SHORT).show()
                                    view.dismiss()
                                }
                                .setCancelable(false)
                                .create()

                            dialogMenu.show()
                        }
                        .setCancelable(false)
                        .create()

                    dialogMenu.show()
                }

            }
        }


    }


    override fun getItemCount() = card.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoV: LinearLayout = itemView.findViewById(R.id.vid)
        val cardTop: CardView = itemView.findViewById(R.id.cardTop)
        val img: ImageView = itemView.findViewById(R.id.img)
        val ct: TextView = itemView.findViewById(R.id.ct)
        val ct2: TextView = itemView.findViewById(R.id.ct2)
        val chip1: Chip = itemView.findViewById(R.id.chip1)
        val chip2: Chip = itemView.findViewById(R.id.chip2)
        val chip3: Chip = itemView.findViewById(R.id.chip3)


    }
}