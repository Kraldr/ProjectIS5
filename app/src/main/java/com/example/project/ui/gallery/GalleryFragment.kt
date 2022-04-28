package com.example.project.ui.gallery

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.primera.content.subCategoriesClass
import com.example.primera.menu.boolNotify
import com.example.primera.menu.cardStart
import com.example.project.*
import com.example.project.databinding.FragmentGalleryBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val database = Firebase.database
    private lateinit var dbref : DatabaseReference
    private lateinit var messagesListener: ValueEventListener
    private lateinit var type: String
    private lateinit var saveEmail: String
    private val listCard:MutableList<cardStart> = ArrayList()
    private val listBool:MutableList<boolNotify> = ArrayList()
    private val subCategories:MutableList<subCategoriesClass> = ArrayList()
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    val myRef = database.getReference("cards")
    private lateinit var item: MenuItem

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        try {
            (activity as MainActivity?)!!.configToolbar()
            val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
            type = sharedPreferences.getString("type", null).toString()
            setupRecyclerView(binding.listRecyclerTop)
        }catch (e:Exception) {

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
                TODO("Not yet implemented")
            }

        })

    }


    private fun datos (recycler:RecyclerView, all: MutableList<cardStart>) {
        dbref = FirebaseDatabase.getInstance().getReference("subCategory")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                subCategories.clear()

                if (snapshot.exists()){

                    for (cardSnapshot in snapshot.children){
                        val card = cardSnapshot.getValue(subCategoriesClass::class.java)
                        if (card != null) {
                            subCategories.add(card)
                        }
                    }

                    recycler.apply {
                        try {
                            layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                            adapter = card_categories_adapter(all, type, requireActivity().applicationContext, subCategories)
                        }catch (e: Exception) {

                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}