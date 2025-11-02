pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // Repositorio específico para osmdroid-compose
        maven { url = uri("https://www.jitpack.io") } // Algunos proyectos de OSM lo usan

        // A menudo, los proyectos de código abierto se alojan aquí también:
        maven { url = uri("https://repo.osmdroid.org/") }
    }
}

rootProject.name = "huertohogarMovil"
include(":app")
 