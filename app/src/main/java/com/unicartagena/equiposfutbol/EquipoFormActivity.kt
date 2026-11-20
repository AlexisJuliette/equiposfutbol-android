package com.unicartagena.equiposfutbol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.unicartagena.equiposfutbol.data.Equipo
import com.unicartagena.equiposfutbol.data.EquipoRequest
import com.unicartagena.equiposfutbol.data.RetrofitClient
import kotlinx.coroutines.launch

class EquipoFormActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtEslogan: EditText
    private lateinit var txtTecnico: EditText
    private lateinit var txtPais: EditText
    private lateinit var txtCiudad: EditText
    private lateinit var txtCategoria: EditText
    private lateinit var txtGoles: EditText
    private lateinit var txtJugados: EditText
    private lateinit var txtGanados: EditText
    private lateinit var txtEmpates: EditText
    private lateinit var txtCampeonatos: EditText
    private lateinit var txtExpulsiones: EditText

    private var equipoId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equipo_form)

        txtNombre = findViewById(R.id.txtNombre)
        txtEslogan = findViewById(R.id.txtEslogan)
        txtTecnico = findViewById(R.id.txtTecnico)
        txtPais = findViewById(R.id.txtPaisForm)
        txtCiudad = findViewById(R.id.txtCiudad)
        txtCategoria = findViewById(R.id.txtCategoria)
        txtGoles = findViewById(R.id.txtGolesForm)
        txtJugados = findViewById(R.id.txtJugados)
        txtGanados = findViewById(R.id.txtGanados)
        txtEmpates = findViewById(R.id.txtEmpates)
        txtCampeonatos = findViewById(R.id.txtCampeonatos)
        txtExpulsiones = findViewById(R.id.txtExpulsiones)

        // Si llega un equipo en el Intent, el formulario entra en modo edicion.
        val json = intent.getStringExtra("equipo")
        if (json != null) {
            val equipo = Gson().fromJson(json, Equipo::class.java)
            equipoId = equipo.id
            findViewById<TextView>(R.id.lblTitulo).text = "Editar equipo"
            txtNombre.setText(equipo.nombre)
            txtEslogan.setText(equipo.eslogan)
            txtTecnico.setText(equipo.tecnico)
            txtPais.setText(equipo.pais)
            txtCiudad.setText(equipo.ciudad)
            txtCategoria.setText(equipo.categoria)
            txtGoles.setText(equipo.goles.toString())
            txtJugados.setText(equipo.partidosJugados.toString())
            txtGanados.setText(equipo.partidosGanados.toString())
            txtEmpates.setText(equipo.empates.toString())
            txtCampeonatos.setText(equipo.campeonatos.toString())
            txtExpulsiones.setText(equipo.expulsiones.toString())
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener { guardar() }
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }
    }

    private fun numero(campo: EditText): Int = campo.text.toString().trim().toIntOrNull() ?: 0

    private fun guardar() {
        val cuerpo = EquipoRequest(
            nombre = txtNombre.text.toString().trim(),
            eslogan = txtEslogan.text.toString().trim(),
            tecnico = txtTecnico.text.toString().trim(),
            pais = txtPais.text.toString().trim(),
            ciudad = txtCiudad.text.toString().trim(),
            categoria = txtCategoria.text.toString().trim(),
            goles = numero(txtGoles),
            partidosJugados = numero(txtJugados),
            partidosGanados = numero(txtGanados),
            empates = numero(txtEmpates),
            campeonatos = numero(txtCampeonatos),
            expulsiones = numero(txtExpulsiones)
        )

        lifecycleScope.launch {
            try {
                val respuesta = if (equipoId == null) {
                    RetrofitClient.api.crearEquipo(cuerpo)
                } else {
                    RetrofitClient.api.actualizarEquipo(equipoId!!, cuerpo)
                }

                if (respuesta.isSuccessful) {
                    Toast.makeText(this@EquipoFormActivity, "Guardado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // El cuerpo del error trae el mensaje de la regla de negocio
                    // que rechazo la operacion (por ejemplo ganados + empates > jugados).
                    val detalle = respuesta.errorBody()?.string() ?: ""
                    Toast.makeText(
                        this@EquipoFormActivity,
                        "Error ${respuesta.code()}: $detalle",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@EquipoFormActivity, "Sin conexion", Toast.LENGTH_LONG).show()
            }
        }
    }
}
