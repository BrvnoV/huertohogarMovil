package com.huertohogar.huertohogarmovil.network.model

import com.huertohogar.huertohogarmovil.model.Producto

object HuertoMapper {

    /**
     * Convierte una lista de FruitDto (API) a una lista de Producto (Tu App).
     */
    fun fromResponseToDomain(fruitList: List<FruitDto>): List<Producto> {
        return fruitList.map { fruitDto ->
            // FIX: Calcula precio como Double y convierte a Int (evita type mismatch)
            val calculatedPriceDouble = fruitDto.nutritions.calories * 10
            val calculatedPrice = calculatedPriceDouble.toInt()  // Convierte a Int
                .coerceAtLeast(700)  // Mínimo 700 CLP (opcional, para precios realistas)
                .coerceAtMost(4000)  // Máximo 4000 CLP (opcional)

            // Lógica para imagen: Intentamos usar el nombre de la fruta en minúsculas
            val imageName = fruitDto.name.lowercase().replace(" ", "_").replace("-", "_")  // Limpia mejor para drawables

            Producto(
                id = fruitDto.id.toString(),
                name = fruitDto.name,
                // Descripción con nutrición (Double se concatena bien como string)
                description = "Familia: ${fruitDto.family}. Azúcar: ${fruitDto.nutritions.sugar}g. Calorías: ${fruitDto.nutritions.calories}",
                price = calculatedPrice,  // Ahora es Int, sin error
                imageName = imageName
            )
        }
    }
}