package com.example.boxview.modelo

// Modelo: Entidades usadas para compatibilidad de datos.
import androidx.room.*

// tablas de versiones anteriores

@Entity(tableName = "despachos")
data class Despacho(@PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bodegaOrigenId: Long, val bodegaDestinoId: Long, val productoId: Long,
    val posicionOrigenId: Long, val cantidad: Int, val preparadorId: Long,
    val estado: String = "PENDIENTE_REVISION", val fecha: Long = System.currentTimeMillis())

@Entity(tableName = "conteos")
data class Conteo(@PrimaryKey val id: Long, val bodegaId: Long, val posicionId: Long,
    val productoId: Long, val asignadoA: Long, val cantidadContada: Int? = null,
    val estado: String = "ASIGNADO", val fecha: Long = System.currentTimeMillis())

@Entity(tableName = "entregas")
data class EntregaAutorizada(@PrimaryKey val id: Long, val bodegaId: Long,
    val productoId: Long, val esperada: Int, val recibida: Int = 0,
    val estado: String = "AUTORIZADA")
