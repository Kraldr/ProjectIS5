package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import com.example.primera.content.subCategoriesClass
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_category.*


private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private lateinit var dialog: Dialog
private var adapters = arrayOf<String?>()
private var UID = ""
private lateinit var txtDescrp:EditText
private lateinit var type:AutoCompleteTextView
private lateinit var cosa: CreateCategory
private val listIcon:MutableList<String> = ArrayList()
private val CHANNEL_ID = "channelTest"

class CreateCategory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        try {
            var btnCrearTipo = findViewById<Button>(R.id.btnCrearTipo)
            var btnRegistro = findViewById<Button>(R.id.btnRegistro)

            setupRecyclerView()

            val txtTitle = findViewById<EditText>(R.id.txtTitle)
            val txtIMG = findViewById<EditText>(R.id.txtIMG)
            txtDescrp = findViewById<EditText>(R.id.txtDescrips)
            UID = UUID.randomUUID().toString()
            type = findViewById<AutoCompleteTextView>(R.id.typeArchive)
            cosa = this@CreateCategory
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.statusBarColor = Color.WHITE;

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


            btnCrearTipo.setOnClickListener {
                val intent = Intent(this, CreateSubCategory::class.java)
                startActivity(intent)

            }

            btnRegistro.setOnClickListener {
                if (txtTitle.text.toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "El titulo no puede ser vacio", Snackbar.LENGTH_LONG)
                        .show()
                }else if (txtIMG.text.toString().contains("http") || txtIMG.text.toString().contains("https://youtu.be/") || txtIMG.text.toString().contains("https://www.youtube.com/")) {
                    setupArchiType()
                }else if (txtDescrp.text.toString().isEmpty()) {
                    Snackbar.make(findViewById(android.R.id.content), "La descripción no puede ser vacia", Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "La URL no es valida", Snackbar.LENGTH_LONG)
                        .show()
                }

            }
        }catch (e:Exception) {

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

    private fun loadSesion () {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_crear)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun saveData (title:String, url:String, descrip:String, typeLocal:String, typeSubTitle:String, typeSelect:String, typeSelectVideo:String) {

        var number = 1


        val database = FirebaseDatabase.getInstance().getReference("content")
        val rnds = (0..listIcon.size).random()
        val databaseBool = FirebaseDatabase.getInstance().getReference("boolNotify")
        val content = contentClass(UID, title, descrip,typeLocal, url, typeSubTitle, typeSelect, typeSelectVideo, listIcon[rnds])
        val contentBool = boolNotify(number.toString(), true, type.text.toString(), txtTitle.text.toString())
        database.child(UID).setValue(content).addOnSuccessListener {
            UID = UUID.randomUUID().toString()
            Toast.makeText(this, "Contenido agregado correctamente", Toast.LENGTH_LONG).show()
            databaseBool.child(number.toString()).setValue(contentBool).addOnSuccessListener {
            }
            dialog.hide()
            finish()
        }

    }

    private fun setupArchiType() {
        dbref = FirebaseDatabase.getInstance().getReference("subCategory")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                SUB_CATEGORIES.clear()
                var typeSubTitle = ""
                var typeSubID = ""

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoriesClass::class.java)
                        if (card != null) {
                            SUB_CATEGORIES.add(card)
                        }
                    }

                    adapters = arrayOf()

                    var idCategory = ""
                    for (i in listCard) {
                        if (i.title == type.text.toString()) {
                            idCategory = i.id
                        }
                    }

                    var count = 0
                    for (i in SUB_CATEGORIES) {
                        if (idCategory == i.category){
                            count++
                            adapters = append(adapters, i.title)
                        }
                    }

                    if (count > 0) {
                        MaterialAlertDialogBuilder(this@CreateCategory)
                        .setTitle("Seleccione una subcategoría")
                        .setPositiveButton("Continuar") { _, _ ->
                            if (typeSubID == "") {
                                for (i in SUB_CATEGORIES) {
                                    if (adapters[0] == i.title) {
                                        typeSubID = i.id
                                        typeSubTitle = i.title
                                    }
                                }
                            }

                            val typeAr = arrayOf("Video", "Imagen")
                            var typeSelect = ""
                            val title = txtTitle.text.toString()
                            var url = txtIMG.text.toString()
                            val descrip = txtDescrp.text.toString()
                            val typeLocal = type.text.toString()
                            MaterialAlertDialogBuilder(this@CreateCategory)
                                .setTitle("Seleccione el tipo de arhivo")
                                .setPositiveButton("Continuar") { _, _ ->
                                    if (typeSelect == "") {
                                        typeSelect = typeAr[0]
                                    }
                                    if (typeSelect == "Video") {
                                        val typeVideo = arrayOf("YouTube", "Otra plataforma")
                                        var typeSelectVideo = ""
                                        MaterialAlertDialogBuilder(this@CreateCategory)
                                            .setTitle("Seleccione el tipo de arhivo")
                                            .setPositiveButton("Continuar") { _, _ ->
                                                if (typeSelectVideo == "") {
                                                    typeSelectVideo = typeVideo[0]
                                                }

                                                MaterialAlertDialogBuilder(this@CreateCategory)
                                                    .setTitle("Información a cargar")
                                                    .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typeLocal\nSubcategoría: $typeSubTitle\nTipo de archivo: $typeSelect\nPlataforma: $typeSelectVideo")
                                                    .setNegativeButton("Cancelar") { dialog, which ->

                                                    }
                                                    .setPositiveButton("Crear") { dialog, which ->
                                                        loadSesion()
                                                        when {
                                                            url.contains("https://www.youtube.com/watch?v=")-> {
                                                                val lstValues: List<String> = url.split("v=").map { it -> it.trim() }
                                                                url = lstValues[1]
                                                            }
                                                            url.contains("https://youtu.be/") -> {
                                                                val lstValues: List<String> = url.split("be/").map { it -> it.trim() }
                                                                url = lstValues[1]
                                                            }
                                                            else -> {
                                                                url = txtIMG.text.toString()
                                                            }
                                                        }
                                                        saveData(title ,url , descrip, idCategory, typeSubID, typeSelect, typeSelectVideo)
                                                    }
                                                    .show()
                                            }
                                            .setSingleChoiceItems(typeVideo, 0) { dialog, which ->
                                                typeSelectVideo = typeVideo[which]
                                            }.show()
                                    }else {
                                        MaterialAlertDialogBuilder(this@CreateCategory)
                                            .setTitle("Información a cargar")
                                            .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typeLocal\nSubcategoría: $typeSubTitle\nTipo de archivo: $typeSelect")
                                            .setNegativeButton("Cancelar") { dialog, which ->
                                                // Respond to negative button press
                                            }
                                            .setPositiveButton("Crear") { dialog, which ->
                                                loadSesion ()
                                                saveData(title ,url , descrip, idCategory, typeSubID, typeSelect, "")
                                            }
                                            .show()
                                    }
                                }
                                .setSingleChoiceItems(typeAr, 0) { dialog, which ->
                                    typeSelect = typeAr[which]
                                }.show()

                        }
                        .setSingleChoiceItems(adapters, 0) { dialog, which ->
                            for (i in listCard) {
                                if (adapters[which] == i.title) {
                                    typeSubID = i.id
                                    typeSubTitle = i.title
                                }
                            }
                        }.show()
                    }else {
                        /*Snackbar.make(findViewById(android.R.id.content), "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", MyUndoListener()).show()*/

                        val snack = Snackbar.make(findViewById(android.R.id.content),"No existe una subcategoría",Snackbar.LENGTH_LONG)
                        snack.setAction("Crear subcategoría", View.OnClickListener {
                            // executed when DISMISS is clicked
                            val intent = Intent(this@CreateCategory, CreateSubCategory::class.java)
                            startActivity(intent)
                        })
                        snack.show()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



    fun append(arr: Array<String?>, element: String): Array<String?> {
        val array = arrayOfNulls<String?>(arr.size + 1)
        System.arraycopy(arr, 0, array, 0, arr.size)
        array[arr.size] = element
        return array
    }

    private fun setupRecyclerView() {
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
                            val text = findViewById<AutoCompleteTextView>(R.id.typeArchive)
                            text.setAdapter(adapters)
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}