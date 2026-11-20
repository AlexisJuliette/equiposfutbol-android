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
import com.unicartagena.equiposfutbol.data.Equipo
import com.unicartagena.equiposfutbol.data.RetrofitClient
import kotlinx.coroutines.launch

class EquiposActivity : AppCompatActivity() {

    private lateinit var lista: ListView
    private lateinit var txtPais: EditText
    private lateinit var txtGoles: EditText

    private var equipos: List<Equipo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipos)

        lista = findViewById(R.id.listaEquipos)
        txtPais = findViewById(R.id.txtPais)
        txtGoles = findViewById(R.id.txtGoles)

        findViewById<Button>(R.id.btnNuevo).setOnClickListener {
            startActivity(Intent(this, EquipoFormActivity::class.java))
        }
        findViewById<Button>(R.id.btnTodos).setOnClickListener { cargarTodos() }
        findViewById<Button>(R.id.btnPorPais).setOnClickListener { reportePorPais() }
        findViewById<Button>(R.id.btnPorGoles).setOnClickListener { reportePorGoles() }

        lista.setOnItemClickListener { _, _, posicion, _ ->
            val intent = Intent(this, EquipoFormActivity::class.java)
            intent.putExtra("equipo", Gson().toJson(equipos[posicion]))
            startActivity(intent)
        }

        lista.setOnItemLongClickListener { _, _, posicion, _ ->
            confirmarBorrado(equipos[posicion])
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
                val respuesta = RetrofitClient.api.listarEquipos()
                if (respuesta.isSuccessful) {
                    pintar(respuesta.body()?.equipos ?: emptyList())
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion con el servidor")
            }
        }
    }

    private fun reportePorPais() {
        val pais = txtPais.text.toString().trim()
        if (pais.isEmpty()) {
            aviso("Escribe un pais")
            return
        }
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.equiposPorPais(pais)
                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()?.equipos ?: emptyList()
                    pintar(datos)
                    aviso("${datos.size} equipos en $pais")
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun reportePorGoles() {
        val minimo = txtGoles.text.toString().trim().toIntOrNull()
        if (minimo == null) {
            aviso("Escribe un numero de goles")
            return
        }
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.equiposPorRendimiento(minimo)
                if (respuesta.isSuccessful) {
                    val datos = respuesta.body()?.equipos ?: emptyList()
                    pintar(datos)
                    aviso("${datos.size} equipos con $minimo goles o mas")
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun confirmarBorrado(equipo: Equipo) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("Seguro que quieres borrar ${equipo.nombre}?")
            .setPositiveButton("Si") { _, _ -> borrar(equipo) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrar(equipo: Equipo) {
        val id = equipo.id ?: return
        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.eliminarEquipo(id)
                if (respuesta.isSuccessful) {
                    aviso("Equipo eliminado")
                    cargarTodos()
                } else {
                    aviso("Error ${respuesta.code()}")
                }
            } catch (e: Exception) {
                aviso("Sin conexion")
            }
        }
    }

    private fun pintar(datos: List<Equipo>) {
        equipos = datos
        val textos = datos.map { "${it.nombre} — ${it.ciudad}, ${it.pais} (${it.goles} goles)" }
        lista.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textos)
    }

    private fun aviso(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
