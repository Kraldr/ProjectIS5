package com.example.project.ui.slideshow

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project.MainActivity
import com.example.project.R
import com.example.project.allUsers
import com.example.project.databinding.FragmentSlideshowBinding
import com.google.firebase.database.*

private lateinit var root: View
private var saveKey:String = ""
private lateinit var dbref : DatabaseReference
private val listUser:MutableList<allUsers> = ArrayList()
private lateinit var toolbar: androidx.appcompat.widget.Toolbar
private lateinit var dialog: Dialog


class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        root = binding.root

        (activity as MainActivity?)!!.configToolbar()

        val btnGuardar: Button = root.findViewById(R.id.btnGuardar)
        val txtEmail = root.findViewById<EditText>(R.id.txtEmail)
        val txtCC = root.findViewById<EditText>(R.id.txtCC)
        val txtNombre = root.findViewById<EditText>(R.id.txtNombre)
        val txtApellido = root.findViewById<EditText>(R.id.txtApellido)
        val txtCel = root.findViewById<EditText>(R.id.txtCel)
        val txtTypeAccount = root.findViewById<EditText>(R.id.txtTypeAccount)

        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        saveKey = sharedPreferences.getString("key", null).toString()


        btnGuardar.setOnClickListener {
            for (i in listUser) {
                if (i.key == saveKey) {
                    if (btnGuardar.text.toString().uppercase() == "ACTUALIZAR DATOS") {
                        txtCC.isEnabled = true
                        txtNombre.isEnabled = true
                        txtApellido.isEnabled = true
                        txtCel.isEnabled = true
                        btnGuardar.text = "GUARDAR DATOS"
                    }else if (btnGuardar.text.toString().uppercase() == "GUARDAR DATOS") {
                        loadSesion()
                        saveData()
                        btnGuardar.text = "ACTUALIZAR DATOS"
                    }
                }
            }

        }

        setupRecyclerView()

        return root
    }

    private fun saveData() {
        val txtEmail = root.findViewById<EditText>(R.id.txtEmail)
        val txtCC = root.findViewById<EditText>(R.id.txtCC)
        val txtNombre = root.findViewById<EditText>(R.id.txtNombre)
        val txtApellido = root.findViewById<EditText>(R.id.txtApellido)
        val txtCel = root.findViewById<EditText>(R.id.txtCel)
        val txtTypeAccount = root.findViewById<EditText>(R.id.txtTypeAccount)

        var dateUpdate:Boolean

        when {
            txtEmail.text.toString() == "" -> {
                dateUpdate = false
            }
            txtCC.text.toString() == "" -> {
                dateUpdate = false
            }
            txtNombre.text.toString() == "" -> {
                dateUpdate = false
            }
            txtApellido.text.toString() == "" -> {
                dateUpdate = false
            }
            txtCel.text.toString() == "" -> {
                dateUpdate = false
            }
            txtTypeAccount.text.toString() == "" -> {
                dateUpdate = false
            }
            else -> {
                dateUpdate = true
            }
        }

        val database = FirebaseDatabase.getInstance().getReference("users")
        val users = allUsers(saveKey, txtEmail.text.toString(), txtCC.text.toString() , txtNombre.text.toString(),txtApellido.text.toString(), txtCel.text.toString() , txtTypeAccount.text.toString(), dateUpdate)
        database.child(saveKey).setValue(users).addOnSuccessListener {
            txtCC.isEnabled = false
            txtNombre.isEnabled = false
            txtApellido.isEnabled = false
            txtCel.isEnabled = false
            Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_LONG).show()
            dialog.hide()
        }
    }

    private fun loadSesion () {
        val context: Context = (activity as MainActivity?)!!.getConext()
        dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_progress_bar_with_crear)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun setupRecyclerView() {
        dbref = FirebaseDatabase.getInstance().getReference("users")
        val txtEmail = root.findViewById<EditText>(R.id.txtEmail)
        val txtCC = root.findViewById<EditText>(R.id.txtCC)
        val txtNombre = root.findViewById<EditText>(R.id.txtNombre)
        val txtApellido = root.findViewById<EditText>(R.id.txtApellido)
        val txtCel = root.findViewById<EditText>(R.id.txtCel)
        val txtTypeAccount = root.findViewById<EditText>(R.id.txtTypeAccount)
        val btnGuardar: Button = root.findViewById(R.id.btnGuardar)
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(allUsers::class.java)
                        if (user != null) {
                            listUser.add(user)
                        }
                    }
                    for (i in listUser) {
                        if (i.key == saveKey) {

                            txtEmail.setText(i.email)
                            txtCC.setText(i.cc)
                            txtNombre.setText(i.name)
                            txtApellido.setText(i.lastname)
                            txtCel.setText(i.cel)
                            txtTypeAccount.setText(i.type)
                            if (i.dateUpdate) {
                                btnGuardar.text ="ACTUALIZAR DATOS"
                            }else {
                                btnGuardar.text == "GUARDAR DATOS"
                            }
                        }
                    }

                }

                if (txtEmail.text.toString() == "") {
                    txtEmail.isEnabled = true
                }

                if (txtCC.text.toString() == "") {
                    txtCC.isEnabled = true
                }

                if (txtNombre.text.toString() == "") {
                    txtNombre.isEnabled = true
                }

                if (txtApellido.text.toString() == "") {
                    txtApellido.isEnabled = true
                }

                if (txtCel.text.toString() == "") {
                    txtCel.isEnabled = true
                }

                if (txtTypeAccount.text.toString() == "") {
                    txtTypeAccount.isEnabled = true
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