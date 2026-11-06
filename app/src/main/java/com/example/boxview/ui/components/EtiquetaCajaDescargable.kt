package com.example.boxview.ui.components

// Componente: Etiqueta de caja para guardar como PDF o imagen.
import com.example.boxview.mensajeErrorUsuario
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.boxview.modelo.Caja
import com.example.boxview.modelo.DetalleCaja
import com.example.boxview.modelo.Producto
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// El QR solo contiene el código único de la caja: nunca montos ni cantidades verificadas.
private fun etiquetaBitmap(caja: Caja, detalles: List<DetalleCaja>, productos: List<Producto>, destino: String): Bitmap {
    val ancho = 1240
    val alto = maxOf(1700, 865 + detalles.size * 82)
    val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 36f }
    fun texto(valor: String, x: Float, y: Float, tamano: Float = 34f, negrita: Boolean = false) {
        p.textSize = tamano
        p.typeface = if (negrita) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
        p.color = Color.BLACK
        // Ajustar textos largos sin truncar códigos/identificadores importantes.
        var escrito = valor
        val margenDisponible = if (y in 180f..520f) minOf(ancho - x - 65, 790f - x) else ancho - x - 65
        while (p.measureText(escrito) > margenDisponible && escrito.length > 5) escrito = escrito.dropLast(1)
        if (escrito.length != valor.length) escrito += "…"
        canvas.drawText(escrito, x, y, p)
    }
    texto("BOXVIEW", 65f, 88f, 58f, true)
    texto(if (caja.tipo == "SALIDA") "ETIQUETA DE DESPACHO" else "RECEPCIÓN PROGRAMADA DE PROVEEDOR", 65f, 143f, 32f)
    texto("Caja: ${caja.codigo}", 65f, 209f, 42f, true)
    texto("Estado: ${caja.estado.replace('_',' ')}", 65f, 262f, 28f)
    texto("Origen: ${caja.origen}", 65f, 319f, 31f)
    texto("Destino: $destino", 65f, 368f, 31f)
    texto("Registro: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(caja.fecha))}", 65f, 419f, 28f)
    if (caja.tipo == "SALIDA") {
        texto("Preparador ID: ${caja.preparadoPor ?: "Pendiente"}   Revisor ID: ${caja.revisorId ?: "Pendiente"}", 65f, 466f, 27f)
    }
    val bits = QRCodeWriter().encode(caja.codigo, BarcodeFormat.QR_CODE, 300, 300,
        mapOf(EncodeHintType.MARGIN to 2))
    val px = Paint().apply { color = Color.BLACK }
    for (y in 0 until bits.height) for (x in 0 until bits.width) {
        if (bits[x,y]) canvas.drawRect((880+x).toFloat(), (185+y).toFloat(),
            (881+x).toFloat(), (186+y).toFloat(), px)
    }
    p.color = Color.LTGRAY
    canvas.drawRect(RectF(60f, 534f, ancho - 60f, 538f), p)
    texto("CONTENIDO ESPERADO POR CAJA", 65f, 589f, 32f, true)
    texto("Código / producto", 65f, 645f, 28f, true)
    texto("Unidades", 990f, 645f, 28f, true)
    detalles.forEachIndexed { i, d ->
        val prod = productos.firstOrNull { it.id == d.productoId }
        val nombre = "${prod?.codigo ?: "ID ${d.productoId}"} · ${prod?.nombre ?: "Producto"}"
        val yy = 706f + i * 82f
        texto(nombre, 65f, yy, 29f)
        texto(d.cantidadEsperada.toString(), 1010f, yy, 32f, true)
        p.color = Color.LTGRAY
        canvas.drawLine(65f, yy + 18, ancho - 65f, yy + 18, p)
    }
    val pie = alto - 65f
    texto("Total esperado: ${detalles.sumOf { it.cantidadEsperada }} unidades", 65f, pie, 32f, true)
    texto("El QR identifica la caja. La recepción exige conteo físico y confirmación.", 65f, pie + 38f, 22f)
    return bitmap
}

private fun guardarEtiqueta(context: Context, uri: Uri, formato: String, caja: Caja,
    detalles: List<DetalleCaja>, productos: List<Producto>, destino: String) {
    require(detalles.isNotEmpty()) { "La caja todavía no tiene contenido" }
    require(formato == "PDF" || formato == "PNG")
    val bitmap = etiquetaBitmap(caja, detalles, productos, destino)
    try {
        context.contentResolver.openOutputStream(uri)?.use { salida ->
            if (formato == "PNG") {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)) { "No se pudo guardar el PNG" }
            } else {
                val doc = PdfDocument()
                try {
                    val anchoPagina = 595
                    val altoPagina = 842
                    // Cada página conserva escala; los documentos con muchos productos se paginan.
                    val pixelesPorPagina = (bitmap.width.toFloat() * altoPagina / anchoPagina).toInt()
                    val paginas = (bitmap.height + pixelesPorPagina - 1) / pixelesPorPagina
                    repeat(paginas) { pagina ->
                        val ini = pagina * pixelesPorPagina
                        val fin = minOf(bitmap.height, ini + pixelesPorPagina)
                        val pdfPage = doc.startPage(PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, pagina + 1).create())
                        val alturaDibujo = (fin - ini).toFloat() / bitmap.width * anchoPagina
                        pdfPage.canvas.drawBitmap(bitmap, Rect(0, ini, bitmap.width, fin),
                            RectF(0f, 0f, anchoPagina.toFloat(), alturaDibujo), Paint(Paint.FILTER_BITMAP_FLAG))
                        doc.finishPage(pdfPage)
                    }
                    doc.writeTo(salida)
                } finally {
                    doc.close()
                }
            }
        } ?: error("No se pudo abrir el destino elegido")
    } finally { bitmap.recycle() }
}

// Genera una etiqueta descargable de caja.
@Composable
fun EtiquetaCajaDescargable(caja: Caja, detalles: List<DetalleCaja>, productos: List<Producto>, destino: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mensaje by remember(caja.codigo) { mutableStateOf("") }
    var trabajando by remember(caja.codigo) { mutableStateOf(false) }
    fun guardar(uri: Uri?, tipo: String) {
        if (uri == null || trabajando) return
        trabajando = true
        scope.launch {
            try {
                withContext(Dispatchers.IO) { guardarEtiqueta(context, uri, tipo, caja, detalles.toList(), productos.toList(), destino) }
                mensaje = "Etiqueta $tipo guardada"
            } catch (e: Exception) { mensaje = mensajeErrorUsuario(e, "No se pudo exportar la etiqueta") }
            finally { trabajando = false }
        }
    }
    val guardarPdf = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { guardar(it, "PDF") }
    val guardarPng = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { guardar(it, "PNG") }
    Column {
        TextButton(enabled = !trabajando && detalles.isNotEmpty(), onClick = { guardarPdf.launch("${caja.codigo}_etiqueta.pdf") }) {
            Text("Guardar etiqueta PDF")
        }
        TextButton(enabled = !trabajando && detalles.isNotEmpty(), onClick = { guardarPng.launch("${caja.codigo}_etiqueta.png") }) {
            Text("Guardar etiqueta PNG")
        }
        if (mensaje.isNotBlank()) Text(mensaje)
    }
}
