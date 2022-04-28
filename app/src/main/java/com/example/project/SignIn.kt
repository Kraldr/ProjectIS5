package com.example.project

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.project.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.regex.Pattern

private lateinit var auth: FirebaseAuth
private lateinit var binding: ActivitySignInBinding
private lateinit var dialog: Dialog

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        supportActionBar?.hide();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.WHITE


        val list = resources.getStringArray(R.array.type)

        val adapters = ArrayAdapter(applicationContext, R.layout.list_item, list)
        val text = findViewById<AutoCompleteTextView>(R.id.typeAccount)
        text.setAdapter(adapters)


        binding.btnRegistro.setOnClickListener {

            val mEmail = binding.txtEmail.text.toString()
            val name: kotlin.String = binding.txtNombre.text.toString()
            val lastname: kotlin.String = binding.txtApellido.text.toString()
            val mPassword = binding.txtPass.text.toString()
            val mRepeatPassword = binding.txtRepeatPass.text.toString()
            val type = findViewById<AutoCompleteTextView>(R.id.typeAccount)

            val passwordRegex = Pattern.compile("^" +
                    "(?=.*[-@#$%^&+/=])" +     // Al menos 1 carácter especial
                    ".{6,}" +                // Al menos 6 caracteres
                    "$")

            if(mEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                Toast.makeText(baseContext, "Ingrese un email valido.",
                    Toast.LENGTH_SHORT).show()
                binding.txtEmail.error = "Verificar correo"
            } else if (mPassword != mRepeatPassword) {
                Toast.makeText(baseContext, "Confirma la contraseña.",
                    Toast.LENGTH_SHORT).show()
                binding.txtPass.error = "Verificar contraseña"
            }else if (type.text.toString() == "") {
                Toast.makeText(baseContext, "Seleccione el tipo de cuenta",
                    Toast.LENGTH_SHORT).show()
                binding.typeAccount.error = "Verificar tipo de cuenta"
            } else {
                loadSesion()
                createAccount(mEmail, mPassword, name, lastname, type.text.toString())
            }

        }

    }

    private fun loadSesion () {

        dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_progress_bar_with_register)
        dialog.show()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    private fun createAccount(email : kotlin.String, password : kotlin.String, name:kotlin.String,lastname:kotlin.String, mType: kotlin.String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val database = FirebaseDatabase.getInstance().getReference("users")
                    var uniqueID = UUID.randomUUID().toString()
                    val users = allUsers(uniqueID, email, "" ,name,lastname, "" ,mType)
                    database.child(uniqueID).setValue(users).addOnSuccessListener {}
                    val intent = Intent(this, MainActivity::class.java)
                    saveData(correo = email, online = true, mType, uniqueID, "$name $lastname")
                    Toast.makeText(baseContext, "Registrado correctamente",
                        Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    dialog.hide()
                    finish()
                } else {
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    dialog.hide()
                }
            }
    }

    private fun saveData (
        correo: String,
        online: Boolean,
        regisType: String,
        key: String,
        s: String
    ) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("correo", correo)
            putString("type", regisType)
            putBoolean("online", online)
            putString("name", s)
            putString("key", key)
        }.apply()
    }

    private fun loadData (): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)
        val saveEmail: kotlin.String = sharedPreferences.getString("correo", null).toString()
        val saveOnline: Boolean = sharedPreferences.getBoolean("online", false)
        return (saveOnline)
    }
}