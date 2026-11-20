package com.unicartagena.equiposfutbol

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.unicartagena.equiposfutbol.data.ActualizarUsuarioRequest
import com.unicartagena.equiposfutbol.data.CrearUsuarioRequest
import com.unicartagena.equiposfutbol.data.RetrofitClient
import com.unicartagena.equiposfutbol.data.Usuario
import kotlinx.coroutines.launch

class UsuarioFormActivity : AppCompatActivity() {

    private lateinit var txtUsername: EditText
    private lateinit var txtClave: EditText
    private lateinit var txtNombre: EditText
    private lateinit var txtEmail: EditText

    private var usuarioId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario_form)

        txtUsername = findViewById(R.id.txtUsername)
        txtClave = findViewById(R.id.txtClave)
        txtNombre = findViewById(R.id.txtNombreU)
        txtEmail = findViewById(R.id.txtEmailU)

        val json = intent.getStringExtra("usuario")
        if (json != null) {
            val usuario = Gson().fromJson(json, Usuario::class.java)
            usuarioId = usuario.id
            findViewById<TextView>(R.id.lblTituloU).text = "Editar usuario"
            txtUsername.setText(usuario.username)
            txtNombre.setText(usuario.nombre)
            txtEmail.setText(usuario.email)

            // El username identifica al usuario en el login y no se edita.
            // La contrasena nunca vuelve del servidor: solo existe su hash.
            txtUsername.isEnabled = false
            txtClave.visibility = View.GONE
        }

        findViewById<Button>(R.id.btnGuardarU).setOnClickListener { guardar() }
        findViewById<Button>(R.id.btnCancelarU).setOnClickListener { finish() }
    }

    private fun guardar() {
        val username = txtUsername.text.toString().trim()
        val clave = txtClave.text.toString()
        val nombre = txtNombre.text.toString().trim()
        val email = txtEmail.text.toString().trim()

        lifecycleScope.launch {
            try {
                val respuesta = if (usuarioId == null) {
                    RetrofitClient.api.crearUsuario(
                        CrearUsuarioRequest(username, clave, nombre, email)
                    )
                } else {
                    RetrofitClient.api.actualizarUsuario(
                        usuarioId!!,
                        ActualizarUsuarioRequest(nombre, email)
                    )
                }

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@UsuarioFormActivity, "Guardado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val detalle = respuesta.errorBody()?.string() ?: ""
                    Toast.makeText(
                        this@UsuarioFormActivity,
                        "Error ${respuesta.code()}: $detalle",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@UsuarioFormActivity, "Sin conexion", Toast.LENGTH_LONG).show()
            }
        }
    }
}
