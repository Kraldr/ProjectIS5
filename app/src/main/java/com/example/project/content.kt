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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primera.content.subCategoriesClass
import com.google.firebase.database.*

private lateinit var dbref : DatabaseReference
private val listCard:MutableList<contentClass> = ArrayList()
private var meesage:String = ""
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private lateinit var dialog: Dialog
private lateinit var type: String

class content : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)

        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        type = sharedPreferences.getString("type", null).toString()
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.WHITE;

        meesage = intent.getStringExtra("Type").toString()
        val recycler = findViewById<RecyclerView>(R.id.recyclerContent)
        loadSesion()


        setupRecyclerView(recycler)
    }

    private fun loadSesion () {

        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_text)
        //dialog.show()
        //dialog.setCancelable(false)
        //dialog.setCanceledOnTouchOutside(false)
    }

    /*override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }*/


    private fun setupRecyclerView(recyclerView: RecyclerView) {
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
                    datos(recyclerView, SUB_CATEGORIES)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datos (recycler:RecyclerView, all: MutableList<subCategoriesClass>) {
        val SUB_CATEGORIES_FILTER:MutableList<subCategoriesClass> = ArrayList()
        for (i in all) {
            if (i.category == meesage) {
                SUB_CATEGORIES_FILTER.add(i)
            }
        }
        val noContent = findViewById<ImageView>(R.id.noContent)
        if (SUB_CATEGORIES_FILTER.size == 0) {
            noContent.visibility = View.VISIBLE
        }else {
            noContent.visibility = View.INVISIBLE
        }
        recycler.apply {
            layoutManager = LinearLayoutManager(this@content)
            adapter = card_subcategories_adapter(SUB_CATEGORIES_FILTER,type, this@content, meesage)
        }

        recycler.layoutManager = GridLayoutManager(this, 1)
    }
}