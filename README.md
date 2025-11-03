🥕 Huerto Hogar - E-commerce Móvil para Productos Frescos

DSY1105 - Desarrollo de Aplicaciones Móviles

Estudiante 1:
Nayaret Rivas

Estudiante 2:
Bruno Valenzuela

Arquitectura

MVVM (Model-View-ViewModel) + UDF (Flujo de Datos Unidireccional)

💡 1. Introducción al Proyecto

Huerto Hogar es un prototipo de aplicación de e-commerce minimalista y funcional, diseñada para la venta y gestión de productos frescos y de huerto (frutas, verduras y hierbas). El proyecto integra las mejores prácticas de desarrollo Android moderno, enfocándose en la modularidad, la mantenibilidad y la experiencia del usuario.

🛠️ 2. Tecnologías y Estructura

El proyecto está construido bajo la arquitectura MVVM para una clara separación de responsabilidades y la utilización de principios de Modularidad y Patrones Arquitectónicos (IE 2.3.2).

Stack Principal

Lenguaje: Kotlin

UI Toolkit: Jetpack Compose (Paradigma Declarativo)

Gestión de Estado: Kotlin Flow (StateFlow y SharedFlow)

Base de Datos Local: Room (SQLite)

Herramienta Colaborativa: Trello (Para la gestión Kanban de tareas)

Arquitectura de Módulos (Separación de Responsabilidades)

Módulo

Responsabilidad

Ejemplo Clave

viewmodel

Gestiona la lógica de la UI y el Estado (StateFlow).

CartViewModel, LoginViewModel.

repository

Fuente de Verdad. Intermedia el acceso a la BD local (AppDatabase).

AppRepositoryImpl.

data/dao

Contratos de la BD (CRUD). Define las operaciones de Room.

ProductoDao, CarritoDao.

screens

Dibuja la Interfaz (Vista) y envía Eventos al ViewModel.

ProductsScreen, ProfileScreen.

location

Acceso a recursos nativos.

Función obtenerUbicacion (Uso del GPS).

✨ 3. Funcionalidades Clave y Logros Técnicos

Hemos implementado los siguientes Indicadores de Evaluación (IL) funcionales:

A. Diseño y Usabilidad (IL 2.1)

Diseño Unificado: Paleta Verde/Tierra consistente. Uso de Material Design 3.

Diseño Adaptable: Uso de LazyVerticalGrid en el Catálogo, asegurando una correcta visualización en diferentes orientaciones.

Transparencias: Uso de capas semitransparentes (.copy(alpha=0.9f)) en las Cards para mejorar la estética del Login/Perfil.

B. Gestión de Estado y Persistencia (IL 2.2 / IL 2.3)

Validación de Formularios (IE 2.1.1): La lógica de validación (email, contraseña, longitud) está integrada en RegisterViewModel.kt y se aplica en tiempo real, mostrando retroalimentación visual inmediata.

Persistencia Local: Uso de Room para almacenar el catálogo de productos y el estado del Carrito (CarritoItem). El estado se mantiene activo después de la navegación.

Flujo del Carrito: El CartViewModel calcula dinámicamente el totalPrice observando los cambios en la DB.

C. Recursos Nativos (IL 2.4)

GPS y Ubicación: Integración del recurso de Ubicación GPS del dispositivo.

Implementación: Uso de la API nativa de Android (FusedLocationProviderClient) a través de la función obtenerUbicacion para obtener coordenadas.

Visualización: El mapa se renderiza utilizando OpenStreetMap (OSM), demostrando la integración de un sistema de mapas sin dependencia de claves de API de Google.

🏃 4. Cómo Ejecutar el Proyecto

Clonar Repositorio: git clone https://docs.github.com/es/repositories/creating-and-managing-repositories/quickstart-for-repositories

Abrir en Android Studio: Abrir la carpeta raíz como proyecto.

Sincronizar Gradle: Esperar a que la sincronización termine o hacer clic en File > Sync Project with Gradle Files.

Ejecutar: Desplegar en un emulador con Google Play Services o en un dispositivo físico.

Ruta de Prueba:

Registro: Crear un nuevo usuario (las credenciales de prueba han sido eliminadas).

Catálogo: Añadir un par de productos al carrito.

Mapa: Ir a Perfil y presionar "Ver mi ubicación en el mapa" para probar el recurso nativo (GPS).
