package com.kompsia.sketchcolor.model

data class StrokeRecord(
    val color: Int,
    val size: Float,
    val eraser: Boolean,
    val points: MutableList<PointRecord> = mutableListOf()
)
