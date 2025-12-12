# HuertoHogar Móvil
Una aplicación Android desarrollada en Kotlin para la gestión y compra de productos hortícolas orgánicos. Permite a los usuarios explorar, agregar al carrito y realizar compras de frutas y verduras frescas, integrando datos de una API externa para un catálogo dinámico.

## Nombres de los Integrantes
- **Bruno Valenzuela**
- **Nayaret Rivas**  

## Funcionalidades
La app ofrece una experiencia completa para usuarios de un huerto orgánico:
- **Autenticación**: Registro y login de usuarios con validación local (Room DB).
- **Pantalla Principal (Home)**: Muestra productos destacados (5 aleatorios), nombre de usuario y navegación bottom bar.
- **Exploración de Productos**: Grid de productos (locales + API) con filtros por categoría, detalles nutricionales y precios dinámicos.
- **Carrito de Compras**: Agregar/actualizar/eliminar items, resumen con totales y join con detalles de productos.
- **Perfil y Ubicación**: Vista de perfil usuario y servicio de ubicación (para entrega futura).
- **Modo Admin**: Edición de productos (precios, descripciones) vía Room.
- **Offline Support**: Cache en Room DB con pre-poblamiento de 10 productos hardcoded; sync API lazy.
- **UI/UX**: Material3 Design, fondo temático, transparencias, loading states y snackbars para feedback.
- **Integración Externa**: Fetch dinámico de ~104 frutas de Fruityvice API, mapeo a entidades locales, imágenes remotas via Coil (Fruits-360 + Unsplash).

## Endpoints Usados
### Propios (Microservicios)
- No se implementaron microservicios independientes en este proyecto (todo gestionado en la app móvil con Room DB local). Sin embargo, se preparó `HuertoApiService` para futuras integraciones:
  - `GET /api/fruit/all` (placeholder para backend propio; actualmente redirige a externa).

### Externos
- **Fruityvice API** (Pública, gratuita, sin auth):  
  - `https://www.fruityvice.com/api/fruit/all`  
    - Retorna JSON con ~104 frutas (name, id, family, nutritions).  
    - Uso: Fetch en `syncProductosFromFruityvice()` para poblar DB.  
    - Rate limit: Ilimitado para uso no comercial.

## Instrucciones para Ejecutar el Proyecto
1. **Requisitos**:
   - Android Studio (versión Koala o superior).
   - SDK Android API 34+.
   - Emulador o dispositivo físico con Android 8.0+.
   - Internet para sync inicial de API.

2. **Clonar el Repositorio**:
   ```
   git clone https://github.com/tu-usuario/huertohogar-movil.git
   cd huertohogar-movil
   ```

3. **Abrir en Android Studio**:
   - Importa el proyecto como "Open an existing Android Studio project".
   - Sync Gradle (clic en "Sync Now").

4. **Configurar**:
   - En `local.properties` (opcional): Agrega `sdk.dir=/ruta/a/tu/sdk`.
   - Para debug: Habilita USB debugging en dispositivo.

5. **Ejecutar**:
   - Selecciona emulador/dispositivo.
   - Clic en "Run" (Shift + F10).
   - La app se instala y lanza automáticamente.

6. **Build APK para Distribución**:
   - Build → Generate Signed Bundle / APK → APK → Next.
   - Usa el .jks proporcionado (ver sección APK abajo).
   - Output: `app/build/outputs/apk/release/app-release.apk`.

## APK Firmado y Ubicación del Archivo .jks
- **APK**: Archivo firmado disponible en `app/build/outputs/apk/release/huertohogar-movil-release.apk` (generado post-build). Tamaño aproximado: 15 MB. Compatible con Android 8.0+.
- **Archivo .jks (Keystore)**:  
  - Ubicación: `keystore/huertohogar-keystore.jks` (incluido en repo, gitignored para seguridad en producción).  
  - Alias: `huertohogar-key`.  
  - Contraseña: `huerto2025` (cambia en producción).  
  - Uso en build.gradle: Configurado en `signingConfigs.release` para firmas automáticas.

Para regenerar: Keytool → `keytool -genkey -v -keystore huertohogar-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias huertohogar-key`.

## Código Fuente
### App Móvil (Android/Kotlin)
- **Repositorio Principal**: [https://github.com/tu-usuario/huertohogar-movil](https://github.com/tu-usuario/huertohogar-movil) (clona arriba).  
- **Estructura Clave**:
  - `app/src/main/kotlin/com/huertohogar/huertohogarmovil/`: Código fuente (DAOs, Models, Repository, ViewModels, Screens).
  - `app/src/main/res/`: Recursos (drawables, layouts, themes).
  - `build.gradle (app)`: Dependencias (Room, Retrofit, Compose, Coil).
- **Licencia**: MIT (libre uso/modificación).

**Versión**: 1.0.0 (Diciembre 2025).  
