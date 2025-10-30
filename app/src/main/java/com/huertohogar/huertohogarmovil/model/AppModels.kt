package com.huertohogar.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad que representa a un usuario en la base de datos.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val passwordHash: String // Almacenar un hash, no texto plano
)

/**
 * Entidad que representa un producto.
 * Usamos los datos reales que proporcionaste.
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val id: String, // Usaremos IDs de texto (ej: "1", "2", etc.)
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String
)

/**
 * Entidad para el carrito de compras.
 * Relaciona un usuario con un producto y una cantidad.
 */
@Entity(
    tableName = "carrito_items",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("productId")]
)
data class CarritoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val productId: String,
    val quantity: Int
)