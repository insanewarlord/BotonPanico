package com.example.botonpanico

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.telephony.SmsManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PanicButtonScreen()
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PanicButtonScreen() {
    val context = LocalContext.current
    val emergencyNumber = "123" // Número de emergencia
    val contactNumber = "3222340225" // Número de contacto fijo
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var buttonPressed by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
        )
    )

    val buttonColor by animateColorAsState(
        when {
            showSuccess -> Color(0xFF43A047) // Verde éxito
            buttonPressed -> Color(0xFFB71C1C) // Rojo oscuro
            else -> Color.Red
        },
        label = "Animación de color"
    )

    val buttonScale by animateFloatAsState(
        if (buttonPressed) 1.1f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "Animación de rebote"
    )

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            kotlinx.coroutines.delay(2000)
            showSnackbar = false
        }
    }
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            kotlinx.coroutines.delay(1500)
            showSuccess = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(contentAlignment = Alignment.Center) {
            Button(
                onClick = {
                    if (!permissions.allPermissionsGranted) {
                        permissions.launchMultiplePermissionRequest()
                        snackbarMessage = "Se requieren permisos para continuar."
                        showSnackbar = true
                        return@Button
                    }
                    buttonPressed = true
                    isLoading = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        lastLocation = location
                        if (location != null) {
                            val result = handlePanicButton(
                                context,
                                emergencyNumber,
                                contactNumber,
                                location
                            )
                            snackbarMessage = result + "\nUbicación: ${location.latitude}, ${location.longitude}"
                            showSuccess = true
                        } else {
                            snackbarMessage = "No se pudo obtener la ubicación. Asegúrate de tener el GPS activado y los permisos concedidos."
                        }
                        isLoading = false
                        showSnackbar = true
                        buttonPressed = false
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(200.dp)
                    .scale(buttonScale)
                    .padding(16.dp),
                enabled = !isLoading && !showSuccess
            ) {
                if (showSuccess) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Éxito",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                } else if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("¡PÁNICO!", color = Color.White)
                }
            }
        }
        if (showSnackbar) {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                containerColor = Color(0xFFB71C1C)
            ) {
                Text(snackbarMessage, color = Color.White)
            }
        }
    }
}

private fun handlePanicButton(
    context: Context,
    emergencyNumber: String,
    contactNumber: String,
    location: Location
): String {
    // Vibrar
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))

    // Enviar SMS con ubicación
    val message = "¡EMERGENCIA! Mi ubicación actual es: " +
            "http://maps.google.com/?q=${location.latitude},${location.longitude}"
    return try {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(contactNumber, null, message, null, null)
        // Llamar a emergencias
        val intentEmergency = Intent(Intent.ACTION_CALL)
        intentEmergency.data = Uri.parse("tel:$emergencyNumber")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intentEmergency)
        }
        // Llamar al contacto
        val intentContact = Intent(Intent.ACTION_CALL)
        intentContact.data = Uri.parse("tel:$contactNumber")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(intentContact)
        }
        "¡Ubicación enviada y llamando a ambos números!"
    } catch (e: Exception) {
        e.printStackTrace()
        "Error al enviar SMS o llamar."
    }
}