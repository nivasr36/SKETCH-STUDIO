package com.kompsia.sketchcolor.data

data class ArtworkRecord(
    val id: Long,
    val title: String,
    val strokesJson: String,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val updatedAt: Long
)
