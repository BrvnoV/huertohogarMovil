package com.huertohogar.huertohogarmovil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad para la tabla de Usuarios
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val pass: String, // En una app real, esto debería estar hasheado.
    val nombre: String,
    val direccion: String,
    // Guardaremos la latitud y longitud obtenidas del GPS
    val latitud: Double? = null,
    val longitud: Double? = null,
    // Guardaremos la URI de la imagen de perfil como un String
    val imagenPerfilUri: String? = null
)

// Entidad para la tabla de Productos
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    // Añadiremos un campo para una URL o nombre de imagen (para mostrar en la app)
    // Usaremos un nombre de drawable simple por ahora.
    val imagenNombre: String
)
