package com.huertohogar.huertohogarmovil.network

import com.huertohogar.huertohogarmovil.network.model.FruitDto
import retrofit2.Response
import retrofit2.http.GET

interface HuertoApiService {
    /**
     * Esta es la función que debe existir en el Repositorio.
     */
    @GET("api/fruit/all")
    suspend fun getAllFruits(): Response<List<FruitDto>>
}