package com.huertohogar.huertohogarmovil.network.model

import com.google.gson.annotations.SerializedName
import com.huertohogar.huertohogarmovil.model.Producto

// En network/model/HuertosModels.kt (agrega si no existe)
data class FruitDto(
    val name: String,
    val id: Int,
    val family: String,
    val order: String,
    val genus: String,
    val nutritions: NutritionsDto
)

data class NutritionsDto(
    val calories: Double,
    val fat: Double,
    val sugar: Double,
    val carbohydrates: Double,
    val protein: Double
)
// Extensión en HuertosModels.kt (después de data class FruitDto)
fun FruitDto.toProducto(): Producto {
    val precio = (nutritions.calories * 25).toInt().coerceAtLeast(700).coerceAtMost(4000)
    val descripcion = "Fruta de la familia $family, con ${nutritions.calories.toInt()} calorías. Rica en ${nutritions.protein.toInt()}g proteína."
    val imageName = name.lowercase().replace(" ", "_").replace("-", "_")

    return Producto(
        id = id.toString(),
        name = name,
        description = descripcion,
        price = precio,
        imageName = imageName
    )
}