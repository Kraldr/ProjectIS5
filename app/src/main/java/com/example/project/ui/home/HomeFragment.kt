package com.example.project.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.example.project.*
import com.example.project.R
import com.example.project.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.internal.e
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var menuAll: Menu
    private lateinit var type: String

    private val database = Firebase.database
    private lateinit var dbref : DatabaseReference
    private lateinit var messagesListener: ValueEventListener
    private lateinit var saveEmail: String
    private val listCard:MutableList<cardStart> = ArrayList()
    private val listCardTop:MutableList<contentClass> = ArrayList()
    private val listBool:MutableList<boolNotify> = ArrayList()
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    val myRef = database.getReference("cards")
    private lateinit var item: MenuItem

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        try {

            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
            saveEmail = sharedPreferences.getString("correo", null).toString()
            type = sharedPreferences.getString("type", null).toString()
            val recycler = root.findViewById<RecyclerView>(R.id.listRecycler)
            val recyclerTop = root.findViewById<RecyclerView>(R.id.listRecyclerTop)
            val txtSearch = root.findViewById<EditText>(R.id.txtSearch)
            setupRecyclerView(recycler)
            setupRecyclerViewTop(recyclerTop)

            txtSearch.isFocusableInTouchMode = true
            txtSearch.requestFocus()

            txtSearch.setOnKeyListener { _, keyCode, event ->

                when {

                    //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                    ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {


                        if (txtSearch.text.toString().isNotEmpty()) {
                            val intent = Intent( context, Search::class.java).apply {
                                putExtra("contentSearch", txtSearch.text.toString())
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }else {
                            Toast.makeText(requireContext(), "Este campo no puede ser vacio", Toast.LENGTH_SHORT).show()
                        }

                        //return true
                        return@setOnKeyListener true
                    }
                    else -> false
                }


            }


            val myService = Intent(requireActivity().applicationContext, MyService::class.java)
            myService.putExtra("inputExtra", "Cosa");
            requireActivity().startService(myService)
            setupBoolNotify ()

            (activity as MainActivity?)!!.configToolbar()
        }catch (e: Exception) {

        }

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
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

                    datos(recyclerView, listCard)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })

    }

    private fun setupRecyclerViewTop(recyclerView: RecyclerView) {
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
                println(error)
            }

        })

    }

    private fun datosTop (recycler:RecyclerView, all: MutableList<contentClass>) {
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val types = sharedPreferences.getString("type", null).toString()
        val context: Context = (activity as MainActivity?)!!.getConext()
        recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = card_top_adapter(all, context, types, listCard)
        }

    }

    private fun datos (recycler:RecyclerView, all: MutableList<cardStart>) {
        try {
            recycler.apply {
                layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                adapter = card_menu_lis_adapter(all, type, requireActivity().applicationContext)
            }

            recycler.layoutManager = LinearLayoutManager(requireActivity().applicationContext, RecyclerView.HORIZONTAL,false)
        }catch (e:Exception) {

        }
    }


    private fun setupBoolNotify () {
        dbref = FirebaseDatabase.getInstance().getReference("boolNotify")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listBool.clear()

                if (snapshot.exists()){

                    for (boolNotifySnapshot in snapshot.children){
                        val boolNotifys = boolNotifySnapshot.getValue(boolNotify::class.java)
                        if (boolNotifys != null) {
                            listBool.add(boolNotifys)
                        }
                    }

                    if (listBool[0].boolNoti) {
                        val myService = Intent(requireActivity().applicationContext, MyService::class.java)
                        requireActivity().startService(myService)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }

        })

    }


    /*private fun startList() {
        val intent = Intent(this, Inicio::class.java).apply {

        }
        startActivity(intent)
        finish()
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}