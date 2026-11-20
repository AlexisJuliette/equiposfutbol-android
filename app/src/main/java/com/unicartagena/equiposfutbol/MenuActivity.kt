package com.unicartagena.equiposfutbol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        findViewById<Button>(R.id.btnEquipos).setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
        }
        findViewById<Button>(R.id.btnUsuarios).setOnClickListener {
            startActivity(Intent(this, UsuariosActivity::class.java))
        }
        findViewById<Button>(R.id.btnSalir).setOnClickListener {
            finish()
        }
    }
}
