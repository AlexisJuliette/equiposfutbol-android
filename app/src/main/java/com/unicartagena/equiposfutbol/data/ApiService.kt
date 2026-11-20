package com.unicartagena.equiposfutbol.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ---------- AUTENTICACION ----------

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<Usuario>

    @POST("api/v1/auth/recuperar-clave")
    suspend fun recuperarClave(@Body body: RecuperarRequest): Response<Unit>

    // ---------- EQUIPOS ----------

    @GET("api/v1/equipos")
    suspend fun listarEquipos(): Response<EquipoListResponse>

    @POST("api/v1/equipos")
    suspend fun crearEquipo(@Body body: EquipoRequest): Response<Equipo>

    @PUT("api/v1/equipos/{id}")
    suspend fun actualizarEquipo(
        @Path("id") id: String,
        @Body body: EquipoRequest
    ): Response<Equipo>

    @DELETE("api/v1/equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: String): Response<Unit>

    @GET("api/v1/equipos/reportes/por-pais")
    suspend fun equiposPorPais(@Query("pais") pais: String): Response<EquipoListResponse>

    @GET("api/v1/equipos/reportes/por-rendimiento")
    suspend fun equiposPorRendimiento(
        @Query("minimoGoles") minimoGoles: Int
    ): Response<EquipoListResponse>

    // ---------- USUARIOS ----------

    @GET("api/v1/users")
    suspend fun listarUsuarios(): Response<List<Usuario>>

    @POST("api/v1/users")
    suspend fun crearUsuario(@Body body: CrearUsuarioRequest): Response<Usuario>

    @PUT("api/v1/users/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: String,
        @Body body: ActualizarUsuarioRequest
    ): Response<Usuario>

    @DELETE("api/v1/users/{id}")
    suspend fun eliminarUsuario(@Path("id") id: String): Response<Unit>

    @GET("api/v1/users/reportes/por-nombre")
    suspend fun usuariosPorNombre(@Query("fragmento") fragmento: String): Response<List<Usuario>>

    @GET("api/v1/users/reportes/por-fecha")
    suspend fun usuariosPorFecha(
        @Query("desde") desde: String,
        @Query("hasta") hasta: String
    ): Response<List<Usuario>>
}
