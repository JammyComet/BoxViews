package com.example.boxview.ui.components

// Componente: Vista de cámara y lectura de QR.
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@OptIn(ExperimentalGetImage::class)
// Abre la cámara y entrega los QR detectados.
@Composable
fun CamaraEscaner(alLeer: (String) -> Unit,
                  modifier: Modifier = Modifier.fillMaxWidth().height(280.dp)) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var granted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = it
    }
    LaunchedEffect(Unit) { if (!granted) permission.launch(Manifest.permission.CAMERA) }
    if (!granted) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Se necesita permiso de cámara para leer códigos QR y de barras.")
            OutlinedButton(onClick = { permission.launch(Manifest.permission.CAMERA) }) { Text("Permitir cámara") }
        }
        return
    }

    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember { BarcodeScanning.getClient() }
    val delivered = remember { AtomicBoolean(false) }
    val active = remember { AtomicBoolean(true) }
    var boundProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var problemaCamara by remember { mutableStateOf(false) }
    val ultimaLectura by rememberUpdatedState(alLeer)
    DisposableEffect(owner) {
        onDispose {
            active.set(false)
            boundProvider?.unbindAll()
            scanner.close()
            executor.shutdown()
        }
    }

    AndroidView(factory = { ctx ->
        val view = PreviewView(ctx)
        val future = ProcessCameraProvider.getInstance(ctx)
        future.addListener({
            try {
                val provider = future.get()
                if (!active.get()) return@addListener
                boundProvider = provider
                val preview = Preview.Builder().build().also { it.surfaceProvider = view.surfaceProvider }
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                analysis.setAnalyzer(executor) { proxy ->
                    val image = proxy.image
                    if (image == null || delivered.get()) {
                        proxy.close()
                    } else {
                        val input = InputImage.fromMediaImage(image, proxy.imageInfo.rotationDegrees)
                        scanner.process(input)
                            .addOnSuccessListener { codes ->
                                val code = codes.firstOrNull()?.rawValue
                                if (active.get() && !code.isNullOrBlank() && delivered.compareAndSet(false, true)) {
                                    ContextCompat.getMainExecutor(ctx).execute {
                                        if (active.get()) ultimaLectura(code)
                                    }
                                }
                            }
                            .addOnCompleteListener { proxy.close() }
                    }
                }
                provider.unbindAll()
                provider.bindToLifecycle(owner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            } catch (_: Exception) { problemaCamara = true }
        }, ContextCompat.getMainExecutor(ctx))
        view
    }, modifier = modifier)
    if (problemaCamara) Text("No se pudo abrir la cámara. Usa el código manual.",
        color = MaterialTheme.colorScheme.error)
}
