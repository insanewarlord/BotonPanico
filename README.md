# BotonPanico
Botón de Pánico es una app Android en Kotlin que permite al usuario pedir ayuda en emergencias mediante un botón central. Al activarse, envía SMS, realiza llamadas y puede compartir la ubicación. Su diseño es simple e intuitivo, ideal para actuar rápidamente en situaciones de peligro o amenaza.
# 🚨 Botón de Pánico UV

Aplicación nativa desarrollada en Kotlin para Android, que permite enviar rápidamente una alerta con la ubicación actual del usuario a través de un SMS, con el objetivo de brindar asistencia en situaciones de emergencia.

## 📱 Funcionalidades principales

- Envío automático de un SMS con la ubicación del usuario a un número de contacto de emergencia.
- Llamada directa a la línea de emergencia local.
- Activación del botón de pánico desde la interfaz principal.
- Uso del GPS y del servicio de SMS del teléfono.
- Vibración del dispositivo como confirmación de envío.

## 🛠️ Tecnologías y componentes usados

- **Kotlin**: Lenguaje de programación principal.
- **Jetpack Compose**: Para la interfaz de usuario.
- **Google Play Services Location**: Para obtener la ubicación actual.
- **SmsManager**: Para envío de SMS.
- **Sensor de vibración**.
- **Permisos en tiempo de ejecución**: uso de `Accompanist Permissions`.

## 📂 Estructura del proyecto

- `MainActivity.kt`: Lógica principal de la app, UI y manejo de permisos.
- `AndroidManifest.xml`: Declaración de permisos necesarios (`SEND_SMS`, `ACCESS_FINE_LOCATION`, `CALL_PHONE`, `VIBRATE`).
- `build.gradle.kts`: Configuración del proyecto y dependencias.
- `Jetpack Compose`: Interfaz moderna y reactiva.
  
## 🔐 Permisos necesarios

La aplicación requiere los siguientes permisos para funcionar correctamente:

- `android.permission.SEND_SMS`
- `android.permission.ACCESS_FINE_LOCATION`
- `android.permission.CALL_PHONE`
- `android.permission.VIBRATE`

Estos deben ser otorgados manualmente por el usuario durante la ejecución.

## ▶️ Instrucciones de compilación e instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/tu-usuario/boton-panico-uv.git
