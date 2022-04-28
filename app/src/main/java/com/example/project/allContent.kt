package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.firebase.database.*

private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private var meesage:String = ""
private var submeesage:String = ""
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private val listCardTop:MutableList<contentClass> = ArrayList()
private val listCategorie:MutableList<cardStart> = ArrayList()
private lateinit var dialog: Dialog
private lateinit var type: String

class allContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_content)
        try {
            supportActionBar!!.hide()
        }catch (e:Exception) {

        }
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        type = sharedPreferences.getString("type", null).toString()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.WHITE;

        meesage = intent.getStringExtra("Type").toString()
        submeesage = intent.getStringExtra("subType").toString()

        val recycler = findViewById<RecyclerView>(R.id.listRecyclerAll)


        setupRecyclerView(recycler)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        dbref = FirebaseDatabase.getInstance().getReference("content")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCardTop.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val content = cardSnapshot.getValue(contentClass::class.java)
                        if (content != null) {
                            listCardTop.add(content)
                        }
                    }

                    datosTop(recyclerView, listCardTop)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datosTop (recycler:RecyclerView, all: MutableList<contentClass>) {

        dbref = FirebaseDatabase.getInstance().getReference("subCategory")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                SUB_CATEGORIES.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoriesClass::class.java)
                        if (card != null) {
                            SUB_CATEGORIES.add(card)
                        }
                    }
                    dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
                    dbref.addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {
                            listCard.clear()

                            if (snapshot.exists()){

                                for (cardSnapshot in snapshot.children){
                                    val card = cardSnapshot.getValue(cardStart::class.java)
                                    if (card != null) {
                                        listCard.add(card)
                                    }
                                }

                                val listCar_Filter:MutableList<contentClass> = ArrayList()

                                for (i in all) {
                                    if (i.typeSubTitle == submeesage){
                                        listCar_Filter.add(i)
                                    }
                                }

                                val noContent = findViewById<ImageView>(R.id.noContent)
                                if (listCar_Filter.size == 0) {
                                    noContent.visibility = View.VISIBLE
                                }else {
                                    noContent.visibility = View.INVISIBLE
                                }

                                val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
                                val types = sharedPreferences.getString("type", null).toString()
                                recycler.apply {
                                    layoutManager = LinearLayoutManager(this@allContent)
                                    adapter = card_allcontent_adapter(listCar_Filter, this@allContent, types, SUB_CATEGORIES, listCard)
                                }

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}