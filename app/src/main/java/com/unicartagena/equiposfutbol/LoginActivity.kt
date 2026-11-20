package com.unicartagena.equiposfutbol

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.unicartagena.equiposfutbol.data.LoginRequest
import com.unicartagena.equiposfutbol.data.RecuperarRequest
import com.unicartagena.equiposfutbol.data.RetrofitClient
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class LoginActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var txtUsuario: EditText
    private lateinit var txtPassword: EditText
    private lateinit var lblSensores: TextView

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var sensorLuz: Sensor? = null

    private var ultimaSacudida = 0L
    private var lux = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtUsuario = findViewById(R.id.txtUsuario)
        txtPassword = findViewById(R.id.txtPassword)
        lblSensores = findViewById(R.id.lblSensores)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        findViewById<Button>(R.id.btnIngresar).setOnClickListener { ingresar() }
        findViewById<Button>(R.id.btnRecuperar).setOnClickListener { recuperar() }
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        sensorLuz?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    // ---------- SENSORES ----------

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> {
                lux = event.values[0]
                mostrarSensores()
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val fuerza = sqrt(x * x + y * y + z * z)
                val ahora = System.currentTimeMillis()
                if (fuerza > 25 && ahora - ultimaSacudida > 1500) {
                    ultimaSacudida = ahora
                    txtUsuario.setText("")
                    txtPassword.setText("")
                    Toast.makeText(this, "Campos limpiados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun mostrarSensores() {
        val ambiente = if (lux < 15) "poca luz" else "buena luz"
        lblSensores.text = "Luz: %.0f lx (%s) · agita el telefono para limpiar".format(lux, ambiente)
    }

    // ---------- API ----------

    private fun ingresar() {
        val usuario = txtUsuario.text.toString().trim()
        val clave = txtPassword.text.toString()

        if (usuario.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "Escribe usuario y contrasena", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Validando...", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            try {
                val respuesta = RetrofitClient.api.login(LoginRequest(usuario, clave))
                if (respuesta.isSuccessful) {
                    val user = respuesta.body()
                    Toast.makeText(
                        this@LoginActivity,
                        "Bienvenido ${user?.nombre}",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(this@LoginActivity, MenuActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Usuario o contrasena incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Sin conexion con el servidor",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun recuperar() {
        val correo = txtUsuario.text.toString().trim()
        if (!correo.contains("@")) {
            Toast.makeText(this, "Escribe tu correo en el primer campo", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch {
            try {
                RetrofitClient.api.recuperarClave(RecuperarRequest(correo))
                Toast.makeText(
                    this@LoginActivity,
                    "Si el correo existe, recibiras una clave temporal",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Sin conexion", Toast.LENGTH_LONG).show()
            }
        }
    }
}
