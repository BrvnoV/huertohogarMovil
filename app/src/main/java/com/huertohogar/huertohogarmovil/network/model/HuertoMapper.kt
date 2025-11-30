package com.huertohogar.huertohogarmovil.network.model

import com.huertohogar.huertohogarmovil.model.Producto
// --- ¡IMPORTACIÓN FALTANTE AÑADIDA! ---
// Necesitas importar la clase FruitDto para que la función la reconozca.
import com.huertohogar.huertohogarmovil.network.model.FruitDto
// --- FIN DE IMPORTACIÓN ---

object HuertoMapper {

    /**
     * Convierte una lista de FruitDto (API) a una lista de Producto (Tu App).
     */
    fun fromResponseToDomain(fruitList: List<FruitDto>): List<Producto> {
        return fruitList.map { fruitDto ->
            // Lógica para asignar precio: Calorías * 10
            val calculatedPrice = fruitDto.nutritions.calories * 10

            // Lógica para imagen: Intentamos usar el nombre de la fruta en minúsculas
            val imageName = fruitDto.name.lowercase().replace(" ", "_")

            Producto(
                id = fruitDto.id.toString(),
                name = fruitDto.name,
                // Usamos la info nutricional como descripción
                description = "Familia: ${fruitDto.family}. Azúcar: ${fruitDto.nutritions.sugar}g. Calorías: ${fruitDto.nutritions.calories}",
                price = calculatedPrice,
                imageName = imageName
            )
        }
    }
}