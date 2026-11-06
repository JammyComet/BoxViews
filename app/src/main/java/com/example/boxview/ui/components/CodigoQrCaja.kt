package com.example.boxview.ui.components

// Componente: Dibuja el QR asociado a una caja.
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

// Dibuja el código QR de una caja.
@Composable
fun CodigoQrCaja(codigo: String) {
    // control de error al generar el QR
    val bitmap = remember(codigo) {
        runCatching {
            val bits = QRCodeWriter().encode(codigo, BarcodeFormat.QR_CODE, 320, 320,
                mapOf(EncodeHintType.MARGIN to 2))
            val pixels = IntArray(320 * 320) { i ->
                if (bits[i % 320, i / 320]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
            Bitmap.createBitmap(pixels, 320, 320, Bitmap.Config.ARGB_8888)
        }.getOrNull()
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (bitmap != null) {
            Image(bitmap.asImageBitmap(), contentDescription = "Código QR de la caja $codigo",
                modifier = Modifier.size(210.dp))
        } else {
            Text("No se pudo mostrar el QR. La caja puede buscarse por código.")
        }
        Text(codigo, fontWeight = FontWeight.Bold)
    }
}
