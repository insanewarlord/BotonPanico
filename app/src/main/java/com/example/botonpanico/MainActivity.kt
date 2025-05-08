package com.example.botonpanico

import android.Manifest
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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PanicButtonScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val emergencyNumber = "3222340225" // Número de emergencia
    val contactNumber = "3222340225" // Número de contacto personal (cámbialo por el deseado)
    
    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (permissions.allPermissionsGranted) {
                    handlePanicButton(context, emergencyNumber, contactNumber)
                } else {
                    permissions.launchMultiplePermissionRequest()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            Text("¡PÁNICO!")
        }
    }
}

private fun handlePanicButton(context: Context, emergencyNumber: String, contactNumber: String) {
    // Vibrar
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))

    // Obtener ubicación
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                // Enviar SMS con ubicación
                val message = "¡EMERGENCIA! Mi ubicación actual es: " +
                        "http://maps.google.com/?q=${location.latitude},${location.longitude}"
                try {
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(contactNumber, null, message, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Llamar a emergencias
    val intent = Intent(Intent.ACTION_CALL)
    intent.data = Uri.parse("tel:$emergencyNumber")
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        context.startActivity(intent)
    }
}