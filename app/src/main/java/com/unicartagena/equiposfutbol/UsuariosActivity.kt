package com.unicartagena.equiposfutbol

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.unicartagena.equiposfutbol.data.RetrofitClient
import com.unicartagena.equiposfutbol.data.Usuario
import kotlinx.coroutines.launch

class UsuariosActivity : AppCompatActivity() {

    private lateinit var lista: ListView
    private lateinit var txtFragmento: EditText
    private lateinit var txtDesde: EditText
    private lateinit var txtHasta: EditText

    private var usuarios: List<Usuario> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        lista = findViewById(R.id.listaUsuarios)
        txtFragmento = findViewById(R.id.txtFragmento)
        txtDesde = findViewById(R.id.txtDesde)
        txtHasta = findViewById(R.id.txtHasta)

        findViewById<Button>(R.id.btnNuevoUsuario).setOnClickListener {
            startActivity(Intent(this, UsuarioFormActivity::class.java))
        }
        findViewById<Button>(R.id.btnTodosUsuarios).setOnClickListener { cargarTodos() }
        findViewById<Button>(R.id.btnPorNombre).setOnClickListener { reportePorNombre() }
        findViewById<Button>(R.id.btnPorFecha).setOnClickListener { reportePorFecha() }

        lista.setOnItemClickListener { _, _, posicion, _ ->
            val intent = Intent(this, UsuarioFormActivity::class.java)
            intent.putExtra("usuario", Gson().toJson(usuarios[posicion]))
            startActivity(intent)
        }

        lista.setOnItemLongClickListener { _, _, posicion, _ ->
            confirmarBorrado(usuarios[posicion])
            true
        }
    }

    override fun onResume() {
        super.onResume()
        cargarTodos()
    }

    private fun cargarTodos() {
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.listarUsuarios()
                if (respuesta.isSuccessful) {
                    pintar(respuesta.body() ?: emptyList())
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion con el servidor")
            }
        }
    }

    private fun reportePorNombre() {
        val fragmento = txtFragmento.text.toString().trim()
        if (fragmento.isEmpty()) {
            aviso("Escribe parte de un nombre")
            return
        }
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.usuariosPorNombre(fragmento)
                if (respuesta.isSuccessful) {
                    val datos = respuesta.body() ?: emptyList()
                    pintar(datos)
                    aviso("${datos.size} coincidencias")
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun reportePorFecha() {
        val desde = txtDesde.text.toString().trim()
        val hasta = txtHasta.text.toString().trim()
        if (desde.isEmpty() || hasta.isEmpty()) {
            aviso("Escribe las dos fechas (2026-11-01)")
            return
        }
        lifecycleScope.launch {
            try {
                // La API no recibe fechas sueltas sino instantes ISO-8601,
                // por eso se completa la hora y la Z de tiempo universal.
                val respuesta = RetrofitClient.api.usuariosPorFecha(
                    "${desde}T00:00:00Z",
                    "${hasta}T23:59:59Z"
                )
                if (respuesta.isSuccessful) {
                    val datos = respuesta.body() ?: emptyList()
                    pintar(datos)
                    aviso("${datos.size} usuarios registrados en ese rango")
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun confirmarBorrado(usuario: Usuario) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("Seguro que quieres borrar ${usuario.username}?")
            .setPositiveButton("Si") { _, _ -> borrar(usuario) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrar(usuario: Usuario) {
        val id = usuario.id ?: return
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.eliminarUsuario(id)
                if (respuesta.isSuccessful) {
                    aviso("Usuario eliminado")
                    cargarTodos()
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun pintar(datos: List<Usuario>) {
        usuarios = datos
        val textos = datos.map { "${it.nombre} (${it.username}) — ${it.email}" }
        lista.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textos)
    }

    private fun aviso(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
