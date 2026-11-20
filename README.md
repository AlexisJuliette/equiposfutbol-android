# EquiposFutbol — Aplicación Android (Corte 3)

Aplicación móvil nativa en Kotlin que consume una API REST propia desplegada en producción.

**Alexis Cera** · acerap@unicartagena.edu.co
Universidad de Cartagena · Ejercicio 8 — EquiposFutbol

---

## Qué hace

| Requisito | Dónde está |
|---|---|
| Login | `LoginActivity` contra `POST /api/v1/auth/login` |
| Recordatorio de clave por email | `LoginActivity` contra `POST /api/v1/auth/recuperar-clave` |
| CRUD tabla Equipos | `EquiposActivity` + `EquipoFormActivity` |
| CRUD tabla Usuarios | `UsuariosActivity` + `UsuarioFormActivity` |
| Reporte 1 de Equipos | Por país — `/api/v1/equipos/reportes/por-pais?pais=` |
| Reporte 2 de Equipos | Por rendimiento — `/api/v1/equipos/reportes/por-rendimiento?minimoGoles=` |
| Reporte 1 de Usuarios | Por fragmento de nombre — `/api/v1/users/reportes/por-nombre?fragmento=` |
| Reporte 2 de Usuarios | Por fecha de registro — `/api/v1/users/reportes/por-fecha?desde=&hasta=` |
| Sensor 1 | Acelerómetro — agitar el teléfono limpia los campos del login |
| Sensor 2 | Sensor de luz — muestra la luz ambiente en lux |

## El backend

La persistencia no está en el dispositivo: vive en una API REST propia, escrita en Java 21 con Spring Boot y arquitectura hexagonal, sobre PostgreSQL.

- API en producción: https://equiposfutbol-api.onrender.com
- Código del backend: https://github.com/AlexisJuliette/equiposfutbol-api

La API es la misma que sirve a la aplicación del Corte 2 en Kodular. No se modificó ni una línea para este corte: el cliente cambió por completo, el servidor no.

## Cómo instalarla y probarla

### Opción rápida — instalar el APK

1. Descarga `app-debug.apk` de este repositorio.
2. Pásalo al teléfono.
3. Permite la instalación de orígenes desconocidos cuando Android lo pida.
4. Ábrela y entra con un usuario existente.

### Opción completa — compilar desde el código

Necesitas Android Studio (Koala o superior) y JDK 17.

```bash
git clone https://github.com/AlexisJuliette/equiposfutbol-android.git
```

1. Abre la carpeta clonada desde Android Studio con **File → Open**.
2. Espera a que Gradle sincronice. Descarga las dependencias de Retrofit la primera vez.
3. Conecta un teléfono con **Depuración por USB** activada, o arranca un emulador con API 24 o superior.
4. Pulsa **Run** (el triángulo verde).

Para generar el APK: **Build → Build Bundle(s)/APK(s) → Build APK(s)**. Queda en `app/build/outputs/apk/debug/`.

## Primera ejecución: la espera

El servicio de Render está en plan gratuito y se duerme tras 15 minutos sin tráfico. La primera llamada lo despierta y puede tardar cerca de un minuto. Por eso `RetrofitClient` usa tiempos de espera de 90 segundos.

Si quieres evitar la espera en una demostración, abre https://equiposfutbol-api.onrender.com/api/v1/equipos en el navegador un minuto antes.

## Estructura

```
com.unicartagena.equiposfutbol
├── data
│   ├── Modelos.kt          Clases de datos que Gson convierte desde y hacia JSON
│   ├── ApiService.kt       Interfaz con las 14 llamadas de la API
│   └── RetrofitClient.kt   Cliente único, con los tiempos de espera largos
├── LoginActivity.kt        Autenticación, recuperación de clave y los dos sensores
├── MenuActivity.kt         Navegación entre las dos tablas
├── EquiposActivity.kt      Listado, reportes y borrado de equipos
├── EquipoFormActivity.kt   Alta y edición de equipos
├── UsuariosActivity.kt     Listado, reportes y borrado de usuarios
└── UsuarioFormActivity.kt  Alta y edición de usuarios
```

## Decisiones que vale la pena señalar

**Las reglas de negocio no se repiten en el cliente.** La aplicación no valida que los partidos ganados más los empates no superen los jugados: esa invariante vive en el dominio del backend. La app envía los datos y muestra el mensaje que devuelve la API. Duplicar la regla en el cliente significaría dos sitios donde corregirla.

**La contraseña nunca viaja de vuelta.** La base guarda solo el hash BCrypt y ningún endpoint lo expone. Por eso, al editar un usuario, el campo de contraseña se oculta y la actualización envía únicamente nombre y correo.

**El `username` no se edita.** Es la identidad con la que se entra al sistema.

## Limitaciones conocidas

**Los endpoints no exigen autenticación.** El login valida credenciales, pero cualquiera con la URL puede consultar o modificar datos. La solución sería JWT; queda fuera del alcance de la actividad.

**El remitente del correo no está verificado con DKIM.** El dominio `unicartagena.edu.co` lo administra la universidad, así que no es posible publicar los registros que Gmail exige. El correo de recuperación llega, pero puede caer en spam.

**La base de datos gratuita de Render expira el 18 de diciembre de 2026.** Después de esa fecha hay que recrearla y actualizar las variables de entorno del servicio.
