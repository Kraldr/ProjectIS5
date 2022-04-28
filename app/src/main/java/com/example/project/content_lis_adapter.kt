package com.example.project

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase


class content_lis_adapter (private val card: MutableList<contentClass>, private val context: Context, private val type:String) : RecyclerView.Adapter<content_lis_adapter.ViewHolder> () {

    private var count: Int = 0
    private lateinit var dialogMenu: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_list, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cards = card[position]


        holder.txtTitulo.text = cards.title
        holder.txtdescrp.text = cards.descrip

        if (cards.type == "e7b91f56-902f-4aad-aec1-a9b859d18153") {
            holder.play.isVisible = true
            holder.videoV.isVisible = true
            holder.videoV.setVideoURI( Uri.parse(cards.url) )
            var running = false
            holder.videoV.setOnPreparedListener(OnPreparedListener {
                running = true
                Thread {
                    do {
                        holder.txtTitulo.post(Runnable {
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

                holder.cardActive.setOnClickListener {
                    dialogMenu = AlertDialog.Builder(context)
                        .setTitle("Ver, modifcar o eliminar contenido")
                        .setMessage("Seleccione una opción para gestionar su contenido")
                        .setNegativeButton("Editar") { view, _ ->
                            val intent = Intent( context, editContent::class.java).apply {
                                putExtra("key", cards.id)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            view.dismiss()
                        }
                        .setPositiveButton("Ver") { view, _ ->
                            val intent = Intent( context, contentRepro::class.java).apply {
                                putExtra("url", cards.url)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
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
                holder.cardActive.setOnClickListener {
                    val intent = Intent( context, contentRepro::class.java).apply {
                        putExtra("url", cards.url)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                }

            }



        }else {
            holder.img.isVisible = true
            Glide.with(holder.itemView.context).load(cards.url).into(holder.img);

            if (type == "Organizador") {

                holder.cardActive.setOnClickListener {
                    dialogMenu = AlertDialog.Builder(context)
                        .setTitle("Ver, modifcar o eliminar contenido")
                        .setMessage("Seleccione una opción para gestionar su contenido")
                        .setNegativeButton("Editar") { view, _ ->
                            val intent = Intent( context, editContent::class.java).apply {
                                putExtra("key", cards.id)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            view.dismiss()
                        }
                        .setPositiveButton("Ver") { view, _ ->
                            val intent = Intent( context, contentIMG::class.java).apply {
                                putExtra("url", cards.url)
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
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
                holder.cardActive.setOnClickListener {
                    val intent = Intent( context, contentIMG::class.java).apply {
                        putExtra("url", cards.url)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                }

            }
        }

    }

    override fun getItemCount() = card.size

    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoV: VideoView = itemView.findViewById(R.id.videoV)
        val play: ImageView = itemView.findViewById(R.id.play)
        val cardActive: CardView = itemView.findViewById(R.id.imgCard)
        val img: ImageView = itemView.findViewById(R.id.img)
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val txtdescrp: TextView = itemView.findViewById(R.id.txtdescrp)

    }
}