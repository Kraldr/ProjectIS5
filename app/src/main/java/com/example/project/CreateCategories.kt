package com.example.project

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private lateinit var uniqueID: kotlin.String
private lateinit var type: kotlin.String
private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private var adapters = arrayOf<String?>()
private val listTitle:MutableList<String> = ArrayList()
private lateinit var categories:AutoCompleteTextView
private var subCa: String? = ""
private var CaID: String? = ""
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private val listCardTop:MutableList<contentClass> = ArrayList()
private var mProgressDialog: ProgressDialog? = null
private val listIcon:MutableList<String> = ArrayList()

class CreateSubCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_sub_category)

        listIcon.add("https://cdn-icons-png.flaticon.com/512/5781/5781478.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/5782/5782789.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/4256/4256900.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/1043/1043445.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/550/550638.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/893/893097.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/755/755195.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/5783/5783071.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/5778/5778950.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/584/584026.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/584/584056.png")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/639/639365.png")
        listIcon.add("https://cdn-icons.flaticon.com/png/512/5145/premium/5145094.png?token=exp=1651117681~hmac=d1a3adf786ac3f0c587faa1a5acb5780")
        listIcon.add("https://cdn-icons-png.flaticon.com/512/1538/1538255.png")
        listIcon.add("https://cdn-icons.flaticon.com/png/512/3614/premium/3614152.png?token=exp=1651117807~hmac=6bfdff21b173a78671aacce89782bab3")

        var uniqueID = UUID.randomUUID().toString()

        var btnSubir = findViewById<Button>(R.id.btnRegistrar)
        var btnDelete = findViewById<Button>(R.id.btnDelete)
        val list = resources.getStringArray(R.array.typeCategory)
        val adapters = ArrayAdapter(applicationContext, R.layout.list_item, list)
        val text = findViewById<AutoCompleteTextView>(R.id.typeCategory)
        text.setAdapter(adapters)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.WHITE;


        setupArchiTypeRecy()

        btnDelete.setOnClickListener {
            setupRecyclerSub()
        }

        btnSubir.setOnClickListener {
            var txtTitle = findViewById<EditText>(R.id.txtTitle)
            var txtTypeCategory = findViewById<EditText>(R.id.typeCategory)

            if (txtTitle.text.toString() == ""){
                Snackbar.make(findViewById(android.R.id.content), "El título no puede ser vacío", Snackbar.LENGTH_LONG)
                    .show()
            }else if(txtTypeCategory.text.toString() == "") {
                Snackbar.make(findViewById(android.R.id.content), "El tipo no puede ser vacío", Snackbar.LENGTH_LONG)
                    .show()
            }else{
                mProgressDialog = ProgressDialog.show(this@CreateSubCategory, "Cargando", "Espere...", false, false)
                if (txtTypeCategory.text.toString() == "Categoría") {
                    crearType()
                }else if (txtTypeCategory.text.toString() == "Subcategoría"){
                    setupArchiType()
                }
            }
        }
    }


    private fun saveData (correo:String, online:Boolean, type: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("correo", correo)
            putString("type", type)
            putBoolean("online", online)
        }.apply()
    }

    private fun crearType () {
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var uniqueID = UUID.randomUUID().toString()

        val rnds = (0..listIcon.size).random()

        val database = FirebaseDatabase.getInstance().getReference("ArchiType")
        val cards = cardStart(uniqueID, txtTitle.text.toString(), listIcon[rnds])
        database.child(uniqueID).setValue(cards).addOnSuccessListener {
            Toast.makeText(this, "Categoría creada correctamente", Toast.LENGTH_LONG).show()
        }

        mProgressDialog!!.dismiss()
        finish()
    }

    private fun createSub (type: String) {
        var txtTitle = findViewById<EditText>(R.id.txtTitle)
        var uniqueID = UUID.randomUUID().toString()
        val rnds = (0..listIcon.size).random()

        val database = FirebaseDatabase.getInstance().getReference("subCategory")
        val subCategoryClass = subCategoriesClass(uniqueID, txtTitle.text.toString(), listIcon[rnds], type)
        database.child(uniqueID).setValue(subCategoryClass).addOnSuccessListener {
            Toast.makeText(this, "Subcategoría creada correctamente", Toast.LENGTH_LONG).show()
        }

        mProgressDialog!!.dismiss()
        finish()
    }

    private fun setupArchiType() {
        dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCard.clear()
                var type = ""

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(cardStart::class.java)
                        if (card != null) {
                            listCard.add(card)
                            listTitle.clear()
                            for (i in listCard) {
                                listTitle.add(i.title)
                            }
                        }
                    }

                    adapters = arrayOf()

                    for (i in listCard) {
                        adapters = append(adapters, i.title)
                    }

                    MaterialAlertDialogBuilder(this@CreateSubCategory)
                        .setTitle("Seleccione una categoría")
                        .setPositiveButton("Crear") { _, _ ->
                            if (type == "") {
                                for (i in listCard) {
                                    if (adapters[0] == i.title) {
                                        type = i.id
                                    }
                                }
                            }
                            createSub(type)
                        }
                        .setSingleChoiceItems(adapters, 0) { dialog, which ->
                            for (i in listCard) {
                                if (adapters[which] == i.title) {
                                    type = i.id
                                }
                            }
                        }.setCancelable(true).show()


                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setupArchiTypeRecy() {
        dbref = FirebaseDatabase.getInstance().getReference("ArchiType")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listCard.clear()

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(cardStart::class.java)
                        if (card != null) {
                            listCard.add(card)
                            listTitle.clear()
                            for (i in listCard) {
                                listTitle.add(i.title)
                            }
                            val adapters = ArrayAdapter(applicationContext, R.layout.list_item, listTitle)
                            categories = findViewById<AutoCompleteTextView>(R.id.typeDelete)
                            categories.setAdapter(adapters)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun setupRecyclerSub() {
        try {
            if (categories.text.toString().isNotEmpty()){
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

                                        var adapters = arrayOf<String?>()
                                        adapters = arrayOf()

                                        for (i in listCard) {
                                            if (i.title == categories.text.toString() && categories.text.toString()
                                                    .isNotEmpty()) {
                                                CaID = i.id
                                                for (j in SUB_CATEGORIES) {
                                                    if (j.category == i.id) {
                                                        adapters = append(adapters, j.title)
                                                    }
                                                }
                                            }
                                        }

                                        adapters = append(adapters, "Sin subcategoría")

                                        try {
                                            MaterialAlertDialogBuilder(this@CreateSubCategory)
                                                .setTitle("Seleccione el tipo de arhivo")
                                                .setPositiveButton("Continuar") { _, _ ->
                                                    if (subCa!!.isEmpty()) {
                                                        subCa = adapters[0]
                                                    }

                                                    if (subCa == "Sin subcategoría"){
                                                        for (i in listCard) {
                                                            if (i.id == CaID) {
                                                                for (j in SUB_CATEGORIES) {
                                                                    if (j.category == i.id) {
                                                                        val database = FirebaseDatabase.getInstance().reference.child("subCategory").child(
                                                                            j.id
                                                                        );
                                                                        database.removeValue();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        val database = FirebaseDatabase.getInstance().reference.child("ArchiType").child(CaID!!);
                                                        database.removeValue();

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

                                                                    for (j in listCardTop) {
                                                                        if (j.type == CaID) {
                                                                            val database = FirebaseDatabase.getInstance().reference.child("content").child(
                                                                                j.id
                                                                            );
                                                                            database.removeValue();
                                                                        }
                                                                    }

                                                                }

                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                TODO("Not yet implemented")
                                                            }

                                                        })
                                                        Toast.makeText(applicationContext, "Eliminado", Toast.LENGTH_LONG).show()
                                                        finish()
                                                    }else {
                                                        var subcaID = ""
                                                        for (j in SUB_CATEGORIES) {
                                                            if (subCa == j.title) {
                                                                subcaID = j.id
                                                                val database = FirebaseDatabase.getInstance().reference.child("subCategory").child(
                                                                    j.id
                                                                );
                                                                database.removeValue();
                                                            }
                                                        }

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

                                                                    for (j in listCardTop) {
                                                                        if (j.typeSubTitle == subcaID) {
                                                                            val database = FirebaseDatabase.getInstance().reference.child("content").child(
                                                                                j.id
                                                                            );
                                                                            database.removeValue();
                                                                        }
                                                                    }

                                                                }

                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                TODO("Not yet implemented")
                                                            }

                                                        })
                                                        Toast.makeText(applicationContext, "Eliminado", Toast.LENGTH_LONG).show()
                                                        finish()
                                                    }
                                                }
                                                .setSingleChoiceItems(adapters, 0) { dialog, which ->
                                                    subCa = adapters[which]
                                                }.show()
                                        }catch (s :java.lang.Exception) {

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
            }else {
                Snackbar.make(findViewById(android.R.id.content), "Seleccione una categoría", Snackbar.LENGTH_LONG)
                    .show()
            }
        }catch (e: Exception) {

        }

    }

    fun append(arr: Array<String?>, element: String): Array<String?> {
        val array = arrayOfNulls<String?>(arr.size + 1)
        System.arraycopy(arr, 0, array, 0, arr.size)
        array[arr.size] = element
        return array
    }
}