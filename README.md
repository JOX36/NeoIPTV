# NeoIPTV Profesional

Reproductor IPTV para Android con interfaz moderna en Jetpack Compose.

## Características

- 📺 Reproducción de canales IPTV (HLS/DASH)
- 📋 Soporte para listas M3U
- 🔗 Login con Xtream Codes API
- ⭐ Sistema de favoritos
- 🔍 Búsqueda de canales
- 🎨 Interfaz oscura con acentos neón

## Requisitos

- Android 7.0+ (API 24)
- Conexión a internet

## Compilación

```bash
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/`

## Descargar APK

Ve a la pestaña **Actions** → selecciona el último build → descarga el artifact **NeoIPTV-debug**.

## Tech Stack

- Kotlin + Jetpack Compose
- ExoPlayer (Media3)
- Room Database
- Hilt (DI)
- Navigation Compose

## Licencia

Proyecto privado.
