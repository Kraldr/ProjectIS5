package com.example.project

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.cardStart
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
private lateinit var dbref : DatabaseReference
private val listCard:MutableList<cardStart> = ArrayList()
private val list:MutableList<contentClass> = ArrayList()
private val listTitle:MutableList<String> = ArrayList()
private val SUB_CATEGORIES:MutableList<subCategoriesClass> = ArrayList()
private var adapters = arrayOf<String?>()
private lateinit var dialog: Dialog
private var meesage:String = ""
private var UID:String = ""
private var type:String = ""
private lateinit var txtTitle:EditText
private lateinit var txtIMG:EditText
private lateinit var txtDescrp:EditText
private lateinit var typeAuto:AutoCompleteTextView

class editContent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_content)

        var btnActualizar = findViewById<Button>(R.id.btnActualizar)
        meesage = intent.getStringExtra("key").toString()

        txtTitle = findViewById<EditText>(R.id.txtTitle)
        txtIMG = findViewById<EditText>(R.id.txtIMG)
        txtDescrp = findViewById<EditText>(R.id.txtDescrips)
        UID = UUID.randomUUID().toString()
        typeAuto = findViewById<AutoCompleteTextView>(R.id.typeArchive)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.statusBarColor = Color.WHITE;


        setupRecyclerView()
        setupContent()



        btnActualizar.setOnClickListener {
            setupArchiType()
        }


    }

    private fun loadSesion () {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_crear)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun saveData (title:String, url:String, descrip:String, typeLocal:String, typeSubTitle:String?, typeSelect:String, typeSelectVideo:String) {

        var saveIMG = ""
        for (i in list){
            if (i.id == UID){
                saveIMG = i.saveAImg
            }
        }

        val database = FirebaseDatabase.getInstance().getReference("content")
        val content = contentClass(UID, title, descrip,typeLocal, url, typeSubTitle, typeSelect, typeSelectVideo,saveIMG)
        database.child(UID).setValue(content).addOnSuccessListener {
            Toast.makeText(this, "Contenido actualizado correctamente", Toast.LENGTH_LONG).show()
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
                var typeSubID:String? = ""

                if (snapshot.exists()){
                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoriesClass::class.java)
                        if (card != null) {
                            SUB_CATEGORIES.add(card)
                        }
                    }

                    adapters = arrayOf()

                    var idCategory = ""
                    var typeID = ""
                    if (typeAuto.text.toString() == "" || typeAuto.text.toString() == "Seleccione una opción") {
                        for (i in listCard) {
                            if (i.title == type) {
                                idCategory = i.id
                            }
                        }

                        for (i in listCard){
                            if (i.id == idCategory) {
                                typeID = i.title
                            }
                        }

                        if (typeID == "" || typeID == "Seleccione una opción") {
                            for (i in list){
                                val content = i
                                if (i.id == UID) {
                                    typeID = i.type
                                }
                            }
                        }
                    }else {
                        for (i in listCard){
                            if (i.title == typeAuto.text.toString()) {
                                typeID = i.id
                            }
                        }

                    }

                    var count = 0
                    for (i in SUB_CATEGORIES) {
                        if (typeID == i.category){
                            count++
                            adapters = append(adapters, i.title)
                        }
                    }
                    var typeSelectSub:String? = ""
                    var cateDis = ""
                    val typeLocal = typeAuto.text.toString()
                    var cateDisBool = false
                    for (i in list) {
                        if (i.id == UID) {
                            cateDis = i.type
                            for (j in listCard) {
                                if (cateDis == j.id) {
                                    if (typeLocal == j.title || typeLocal == ""){
                                        cateDisBool = true
                                    }
                                }
                            }
                        }
                    }
                    if (cateDisBool) {
                        adapters = append(adapters, "Sin modificaciones")
                    }

                    MaterialAlertDialogBuilder(this@editContent)
                        .setTitle("Seleccione una subcategoría")
                        .setPositiveButton("Continuar") { _, _ ->
                            val typeAr = arrayOf("Video", "Imagen")
                            var typeSelect = ""
                            val title = txtTitle.text.toString()
                            var url = txtIMG.text.toString()
                            val descrip = txtDescrp.text.toString()
                            val typeLocal = typeAuto.text.toString()

                            if (typeSelectSub == "") {
                                typeSelectSub = adapters[0]
                            }

                            for (i in SUB_CATEGORIES){
                                if (i.title == typeSelectSub) {
                                    typeSubID = i.id
                                    typeSubTitle = i.title
                                    break
                                }
                            }

                            if (typeSelectSub == "Sin modificaciones") {
                                for (i in list) {
                                    if (i.id == UID) {
                                        url = when {
                                            url.contains("https://www.youtube.com/watch?v=")-> {
                                                val lstValues: List<String> = url.split("v=").map { it -> it.trim() }
                                                lstValues[1]
                                            }
                                            url.contains("https://youtu.be/") -> {
                                                val lstValues: List<String> = url.split("be/").map { it -> it.trim() }
                                                lstValues[1]
                                            }
                                            else -> {
                                                txtIMG.text.toString()
                                            }
                                        }
                                        loadSesion()
                                        saveData(title ,url , descrip, i.type, i.typeSubTitle, i.typeSelect, i.typeSelectVideo)
                                    }
                                }
                            }else {
                                MaterialAlertDialogBuilder(this@editContent)
                                    .setTitle("Seleccione el tipo de arhivo")
                                    .setPositiveButton("Continuar") { _, _ ->
                                        if (typeSelect == "") {
                                            typeSelect = typeAr[0]
                                        }
                                        if (typeSelect == "Video") {
                                            val typeVideo = arrayOf("YouTube", "Otra plataforma")
                                            var typeSelectVideo = ""
                                            MaterialAlertDialogBuilder(this@editContent)
                                                .setTitle("Seleccione el tipo de arhivo")
                                                .setPositiveButton("Continuar") { _, _ ->
                                                    if (typeSelectVideo == "") {
                                                        typeSelectVideo = typeVideo[0]
                                                    }
                                                    var typetitle = ""
                                                    if (typeID == "" || typeID == "Selecione una opción") {
                                                        for (i in list){
                                                            if (i.id == UID) {
                                                                typeID = i.type
                                                            }
                                                        }
                                                    }
                                                    for (i in listCard) {
                                                        if (i.id == typeID) {
                                                            typetitle = i.title
                                                        }
                                                    }
                                                    MaterialAlertDialogBuilder(this@editContent)
                                                        .setTitle("Información a cargar")
                                                        .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typetitle\nSubcategoría: $typeSelectSub\nTipo de archivo: $typeSelect\nPlataforma: $typeSelectVideo")
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

                                                            saveData(title ,url , descrip, typeID, typeSubID, typeSelect, typeSelectVideo)
                                                        }
                                                        .show()
                                                }
                                                .setSingleChoiceItems(typeVideo, 0) { dialog, which ->
                                                    typeSelectVideo = typeVideo[which]
                                                }.show()
                                        }else {
                                            MaterialAlertDialogBuilder(this@editContent)
                                                .setTitle("Información a cargar")
                                                .setMessage("UID: $UID\nTitulo: $title\nURL: $url\nDescripción: $descrip\nCategoría: $typeLocal\nSubcategoría: $typeSubTitle\nTipo de archivo: $typeSelect")
                                                .setNegativeButton("Cancelar") { dialog, which ->
                                                    // Respond to negative button press
                                                }
                                                .setPositiveButton("Crear") { dialog, which ->
                                                    loadSesion ()
                                                    saveData(title ,url , descrip, typeID, typeSubID, typeSelect, "")
                                                }
                                                .show()
                                        }
                                    }
                                    .setSingleChoiceItems(typeAr, 0) { dialog, which ->
                                        typeSelect = typeAr[which]
                                    }.setCancelable(true).show()
                            }
                        }
                        .setSingleChoiceItems(adapters, 0) { dialog, which ->
                            typeSelectSub = adapters[which]
                        }.show()
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

    private fun setupContent() {
        dbref = FirebaseDatabase.getInstance().getReference("content")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val content = cardSnapshot.getValue(contentClass::class.java)
                        if (content != null) {
                            list.add(content)
                        }
                    }
                    datos(list)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun datos(listCard: MutableList<contentClass>) {
        val txtTitle = findViewById<EditText>(R.id.txtTitle)
        val txtIMG = findViewById<EditText>(R.id.txtIMG)
        val txtDescrp = findViewById<EditText>(R.id.txtDescrips)

        for (i in listCard) {
            if (i.id == meesage) {
                txtTitle.setText(i.title)
                txtIMG.setText(i.url)
                txtDescrp.setText(i.descrip)
                UID = i.id
                type = i.type
            }
        }
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