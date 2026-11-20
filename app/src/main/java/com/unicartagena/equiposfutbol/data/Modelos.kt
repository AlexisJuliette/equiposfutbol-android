package com.unicartagena.equiposfutbol.data

// ---------- EQUIPOS ----------

data class Equipo(
    val id: String? = null,
    val nombre: String = "",
    val eslogan: String = "",
    val tecnico: String = "",
    val pais: String = "",
    val ciudad: String = "",
    val categoria: String = "",
    val goles: Int = 0,
    val partidosJugados: Int = 0,
    val partidosGanados: Int = 0,
    val empates: Int = 0,
    val partidosPerdidos: Int = 0,
    val campeonatos: Int = 0,
    val expulsiones: Int = 0,
    val promedioGoles: Double = 0.0
)

data class EquipoListResponse(
    val equipos: List<Equipo> = emptyList(),
    val total: Int = 0
)

data class EquipoRequest(
    val nombre: String,
    val eslogan: String,
    val tecnico: String,
    val pais: String,
    val ciudad: String,
    val categoria: String,
    val goles: Int,
    val partidosJugados: Int,
    val partidosGanados: Int,
    val empates: Int,
    val campeonatos: Int,
    val expulsiones: Int
)

// ---------- USUARIOS ----------

data class Usuario(
    val id: String? = null,
    val username: String = "",
    val nombre: String = "",
    val email: String = ""
)

data class CrearUsuarioRequest(
    val username: String,
    val password: String,
    val nombre: String,
    val email: String
)

data class ActualizarUsuarioRequest(
    val nombre: String,
    val email: String
)

// ---------- AUTENTICACION ----------

data class LoginRequest(
    val username: String,
    val password: String
)

data class RecuperarRequest(
    val email: String
)
