package com.huertohogar.huertohogarmovil.ui.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Esta anotación es necesaria para usar las funciones de testing de coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class UbicacionViewModelTest {

    private lateinit var viewModel: UbicacionViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    // Coordenadas de prueba para verificar la actualización
    private val TEST_LAT = 34.0522 // Los Angeles
    private val TEST_LON = -118.2437

    @BeforeEach
    fun setup() {
        // 1. Establecer el Dispatcher
        Dispatchers.setMain(testDispatcher)

        // 2. Inicializar el ViewModel.
        viewModel = UbicacionViewModel()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar, las coordenadas deben ser las por defecto (Santiago)`() = runTest {
        // GIVEN: El ViewModel inicializado

        // WHEN: Obtenemos el estado inicial
        val latitudInicial = viewModel.latitud
        val longitudInicial = viewModel.longitud

        // THEN: El estado debe coincidir con el DEFAULT_GEOPOINT
        assertEquals(DEFAULT_GEOPOINT.latitude, latitudInicial, 0.0001, "La latitud inicial debe ser la de Santiago.")
        assertEquals(DEFAULT_GEOPOINT.longitude, longitudInicial, 0.0001, "La longitud inicial debe ser la de Santiago.")
    }

    @Test
    fun `la funcion actualizarUbicacion debe actualizar las coordenadas del estado`() = runTest {
        // GIVEN: El ViewModel listo

        // WHEN: Se simula la recepción de coordenadas GPS
        viewModel.actualizarUbicacion(TEST_LAT, TEST_LON)

        // THEN: El estado observable debe reflejar las nuevas coordenadas
        assertEquals(TEST_LAT, viewModel.latitud, 0.0001, "La latitud debe ser actualizada a la coordenada de prueba.")
        assertEquals(TEST_LON, viewModel.longitud, 0.0001, "La longitud debe ser actualizada a la coordenada de prueba.")
    }
}