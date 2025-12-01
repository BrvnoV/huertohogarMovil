package com.huertohogar.huertohogarmovil.network.model

import com.google.gson.annotations.SerializedName

// Estructura de la información nutricional (anidada en el JSON)
data class NutritionDto(
    @SerializedName("calories") val calories: Int,
    @SerializedName("fat") val fat: Double,
    @SerializedName("sugar") val sugar: Double,
    @SerializedName("carbohydrates") val carbohydrates: Double,
    @SerializedName("protein") val protein: Double
)

// Estructura principal de la fruta (DTO - Data Transfer Object)
data class FruitDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("genus") val genus: String,
    @SerializedName("family") val family: String,
    @SerializedName("order") val order: String,
    @SerializedName("nutritions") val nutritions: NutritionDto
)