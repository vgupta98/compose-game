package io.github.vgupta98.compose_game.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorPainter

data class RoundedObjectResource(
    override val id: Int,
    val painter: VectorPainter
): GameResource()

data class BoundaryResource(
    override val id: Int,
    val color: Color,
    val thicknessInPx: Float,
): GameResource()