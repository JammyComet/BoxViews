package com.example.boxview.ui.components

// Componente: Crea los informes PDF con el lienzo de Android.
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

/** Informes A4 nativos, sin dependencias externas. No muestra precios en las vistas de bodega. */
data class ColumnaInforme(val nombre: String, val peso: Float)

class InformePdf(private val titulo: String, private val subtitulo: String, private val area: String) {
    private val document = PdfDocument()
    private val w = 595f
    private val h = 842f
    private val m = 40f
    private val inner = w - 2 * m
    private val brown = Color.rgb(109, 76, 58)
    private val dark = Color.rgb(52, 43, 38)
    private val muted = Color.rgb(107, 100, 96)
    private val cream = Color.rgb(248, 244, 239)
    private val border = Color.rgb(226, 215, 204)
    private val green = Color.rgb(43, 115, 94)
    private val fmt = NumberFormat.getIntegerInstance(Locale("es", "CL"))
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var y = 0f
    private var pages = 0
    private val sello = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date())

    fun moneda(n: Long): String = "$" + fmt.format(n) + " CLP"
    fun numero(n: Number): String = fmt.format(n)

    private fun texto(s: String, x: Float, yy: Float, size: Float = 10f,
                      color: Int = dark, bold: Boolean = false, align: Paint.Align = Paint.Align.LEFT) {
        paint.apply {
            style = Paint.Style.FILL
            this.color = color
            textSize = size
            typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
            textAlign = align
        }
        canvas!!.drawText(s, x, yy, paint)
    }
    private fun fill(color: Int, left: Float, top: Float, right: Float, bottom: Float, radius: Float = 0f) {
        paint.style = Paint.Style.FILL
        paint.color = color
        canvas!!.drawRoundRect(RectF(left, top, right, bottom), radius, radius, paint)
    }
    private fun line(yLine: Float) = fill(border, m, yLine, w - m, yLine + 0.8f)

    private fun newPage() {
        closePage()
        pages++
        page = document.startPage(PdfDocument.PageInfo.Builder(w.toInt(), h.toInt(), pages).create())
        canvas = page!!.canvas
        fill(brown, 0f, 0f, w, 94f)
        texto("BOXVIEW", m, 30f, 14f, Color.WHITE, true)
        texto(area.uppercase(Locale("es", "CL")), w - m, 30f, 8.5f, Color.WHITE, true, Paint.Align.RIGHT)
        texto(if (pages == 1) titulo else "$titulo (continuación)", m, 58f, 16f, Color.WHITE, true)
        texto(if (pages == 1) subtitulo else subtitulo, m, 76f, 9f, Color.WHITE)
        y = 118f
    }
    private fun closePage() {
        if (page != null) {
            line(h - 39f)
            texto("BOXVIEW · $sello", m, h - 23f, 8f, muted)
            texto("Página $pages", w - m, h - 23f, 8f, muted, align = Paint.Align.RIGHT)
            document.finishPage(page!!)
            page = null
            canvas = null
        }
    }
    private fun ensure(heightNeeded: Float) {
        if (page == null || y + heightNeeded > h - 54f) newPage()
    }
    private fun words(text: String, maxWidth: Float, size: Float, bold: Boolean = false): List<String> {
        paint.textSize = size
        paint.typeface = if (bold) Typeface.create(Typeface.DEFAULT, Typeface.BOLD) else Typeface.DEFAULT
        val list = mutableListOf<String>()
        text.replace('\n', ' ').trim().split(Regex("\\s+")).forEach { token ->
            if (token.isBlank()) return@forEach
            val current = list.lastOrNull()
            if (current != null && paint.measureText("$current $token") <= maxWidth) {
                list[list.lastIndex] = "$current $token"
            } else if (paint.measureText(token) <= maxWidth) {
                list.add(token)
            } else {
                var rest = token
                while (rest.isNotEmpty()) {
                    var k = rest.length
                    while (k > 1 && paint.measureText(rest.substring(0, k)) > maxWidth) k--
                    list.add(rest.substring(0, k))
                    rest = rest.substring(k)
                }
            }
        }
        return list.ifEmpty { listOf("—") }
    }
    fun seccion(t: String) {
        ensure(45f)
        y += 13f
        texto(t, m, y + 14f, 13f, brown, true)
        y += 27f
        line(y)
        y += 10f
    }
    fun nota(t: String) {
        val wrapped = words(t, inner - 22, 9f)
        val height = max(34f, wrapped.size * 13f + 17f)
        ensure(height + 10f)
        fill(cream, m, y, w - m, y + height, 8f)
        wrapped.forEachIndexed { i, str -> texto(str, m + 11f, y + 16f + i * 13f, 9f, muted) }
        y += height + 12f
    }
    fun metricas(valores: List<Pair<String, String>>) {
        valores.chunked(2).forEach { row ->
            ensure(76f)
            val width = (inner - 12f) / 2f
            row.forEachIndexed { i, (label, value) ->
                val x = m + (width + 12f) * i
                fill(cream, x, y, x + width, y + 66f, 10f)
                words(label, width - 20f, 8.5f).take(2).forEachIndexed { n, t ->
                    texto(t, x + 11f, y + 16f + n * 10f, 8.5f, muted)
                }
                val numSize = if (value.length > 20) 11f else if (value.length > 14) 13f else 16f
                texto(value, x + 11f, y + 51f, numSize, brown, true)
            }
            y += 77f
        }
    }
    fun barras(titulo: String, valores: List<Pair<String, Long>>, moneda: Boolean = false) {
        if (valores.isEmpty()) { nota("No hay registros suficientes para este gráfico."); return }
        seccion(titulo)
        val top = valores.sortedByDescending { it.second }.take(12)
        val max = max(1L, top.maxOf { it.second })
        top.forEach { (name, amount) ->
            ensure(46f)
            val label = words(name, inner * .56f, 9f).first()
            texto(label, m, y + 11f, 9f, dark, true)
            texto(if (moneda) moneda(amount) else numero(amount), w - m, y + 11f, 9f, brown, true, Paint.Align.RIGHT)
            fill(cream, m, y + 20f, w - m, y + 30f, 4f)
            val barW = (inner * (amount.toDouble() / max.toDouble())).toFloat().coerceIn(0f, inner)
            if (barW > 0f) fill(green, m, y + 20f, m + barW, y + 30f, 4f)
            y += 43f
        }
        
    }
    fun tabla(titulo: String, columns: List<ColumnaInforme>, rows: List<List<String>>, emptyText: String = "Sin registros para este apartado") {
        require(columns.isNotEmpty() && columns.all { it.peso > 0f })
        seccion(titulo)
        if (rows.isEmpty()) { nota(emptyText); return }
        val total = columns.sumOf { it.peso.toDouble() }.toFloat()
        val widths = columns.map { inner * it.peso / total }
        fun tableHeader() {
            ensure(32f)
            fill(brown, m, y, w - m, y + 27f, 5f)
            var x = m
            columns.forEachIndexed { i, c ->
                val words = words(c.nombre, widths[i] - 14f, 8f, true).take(2)
                words.forEachIndexed { ix, s -> texto(s, x + 7f, y + 11f + ix * 9f, 8f, Color.WHITE, true) }
                x += widths[i]
            }
            y += 28f
        }
        tableHeader()
        rows.forEachIndexed { rowIndex, row ->
            val cells = columns.indices.map { i -> words(row.getOrNull(i).orEmpty(), widths[i] - 14f, 8f) }
            val rowHeight = max(24f, cells.maxOf { it.size } * 11f + 12f)
            if (y + rowHeight > h - 54f) { newPage(); tableHeader() }
            if (rowIndex % 2 == 0) fill(cream, m, y, w - m, y + rowHeight)
            var x = m
            cells.forEachIndexed { ix, lines ->
                lines.forEachIndexed { n, s -> texto(s, x + 7f, y + 12f + n * 11f, 8f, dark) }
                x += widths[ix]
            }
            y += rowHeight
        }
        y += 11f
    }
    fun guardar(context: Context, uri: Uri) {
        try {
            if (page == null) newPage()
            closePage()
            context.contentResolver.openOutputStream(uri, "w")?.use { output -> document.writeTo(output) }
                ?: error("No se pudo crear el PDF")
        } finally {
            document.close() // PdfDocument no implementa Closeable; no usar .use.
        }
    }
}

/** Si no hay lector instalado, el PDF queda guardado y puede abrirse desde Archivos o un PC. */
fun abrirInformePdf(context: Context, uri: Uri): String? = try {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(intent)
    null
} catch (_: ActivityNotFoundException) {
    "El PDF está guardado. Instala un lector de PDF o ábrelo desde tu computador."
} catch (e: Exception) {
    "El PDF está guardado, pero no se pudo abrir. Puedes buscarlo en Archivos."
}
