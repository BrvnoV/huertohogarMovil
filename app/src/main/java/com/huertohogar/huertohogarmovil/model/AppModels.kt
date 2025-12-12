package com.huertohogar.huertohogarmovil.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Entidad que representa a un usuario en la base de datos local.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val passwordHash: String // En una app real, esto sería un hash seguro
)

/**
 * Entidad que representa un producto del huerto.
 * Esta es la clase que te faltaba.
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val id: String, // ID único del producto (puede venir de la API)
    val name: String,
    val description: String,
    val price: Int,
    val imageName: String // Nombre del recurso en drawable (ej: "tomate_cherry")
)

data class FrutaApi(
    val name: String,
    val id: Int,
    val family: String,
    val order: String,
    val genus: String,
    val nutritions: Nutritions
)

data class Nutritions(
    val calories: Double,
    val fat: Double,
    val sugar: Double,
    val carbohydrates: Double,
    val protein: Double
)
/**
 * Entidad para el carrito de compras.
 * Relaciona un usuario con un producto y guarda la cantidad.
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

/**
 * Clase de relación para obtener el Carrito con los detalles del Producto.
 * Esto es lo que usa la UI del Carrito para mostrar nombre y precio.
 */

data class CarritoItemConDetalles(
    @androidx.room.Embedded
    val carritoItem: CarritoItem,

    @androidx.room.Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val producto: Producto
)