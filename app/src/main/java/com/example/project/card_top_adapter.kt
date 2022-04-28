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
import com.example.primera.menu.cardStart
import com.google.android.youtube.player.YouTubeStandalonePlayer
import com.google.firebase.database.FirebaseDatabase

class card_top_adapter(
    private val card: MutableList<contentClass>,
    private val context: Context?,
    private val type: String,
    private val listcard: MutableList<cardStart>,
) : RecyclerView.Adapter<card_top_adapter.ViewHolder> () {

    private var count: Int = 0
    private lateinit var dialogMenu: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_top, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cards = card[position]
        Glide.with(holder.itemView.context).load(cards.url).into(holder.img);
        holder.ct.text = cards.title
        for (i in listcard) {
            if (i.id == cards.type) {
                holder.ct2.text = i.title
            }
        }

        if (cards.typeSelect == "Video") {

            if(cards.typeSelectVideo == "YouTube"){
                holder.img.visibility = View.VISIBLE
                Glide.with(holder.itemView.context).load(cards.saveAImg).into(holder.img);
            }else {
                holder.videoV.isVisible = true
                holder.videoV.setVideoURI( Uri.parse(cards.url) )
            }
            var running = false
            holder.videoV.setOnPreparedListener(MediaPlayer.OnPreparedListener {
                running = true
                Thread {
                    do {
                        holder.ct.post(Runnable {
                            val time: Int = (holder.videoV.duration / 2) - 10
                            holder.videoV.seekTo(time)
                            count++
                        })
                        try {
                            Thread.sleep(500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        if (!running) break
                    } while (count <= card.size)
                }.start()
            })

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
                holder.cardTop.setOnClickListener {
                    val intent = Intent( context, contentRepro::class.java).apply {
                        putExtra("url", cards.url)
                        putExtra("typeSelectedVideo", cards.typeSelectVideo)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent)

                }

            }



        }else {
            holder.img.isVisible = true
            Glide.with(holder.itemView.context).load(cards.url).into(holder.img);

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
                            val intent = Intent( context, contentIMG::class.java).apply {
                                putExtra("url", cards.url)
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
                        .setCancelable(true)
                        .create()

                    dialogMenu.show()
                }

            }else {
                holder.cardTop.setOnClickListener {
                    val intent = Intent( context, contentIMG::class.java).apply {
                        putExtra("url", cards.url)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context?.startActivity(intent)

                }

            }
        }

    }


    override fun getItemCount() = card.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoV: VideoView = itemView.findViewById(R.id.videoView)
        val cardTop: CardView = itemView.findViewById(R.id.cardTop)
        val img: ImageView = itemView.findViewById(R.id.img)
        val ct: TextView = itemView.findViewById(R.id.ct)
        val ct2: TextView = itemView.findViewById(R.id.ct2)

    }
}