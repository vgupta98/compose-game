package io.github.vgupta98.compose_game.presentation.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorPainter

/**
 * A data class representing the visual resource for a round object in the game.
 *
 * @property id The unique identifier for this resource. Keep this [id] same as the [id] passed in [GameObject].
 * @property painter A [VectorPainter] used to draw the round object.
 */
data class RoundObjectResource(
    override val id: Int,
    val painter: VectorPainter
): GameResource()

/**
 * A data class representing the visual resource for a boundary in the game. Keep this [id] same as the [id] passed in [GameObject].
 *
 * @property id The unique identifier for this resource.
 * @property color The color used to draw the boundary.
 * @property thicknessInPx The thickness of the boundary in pixels.
 */
data class BoundaryResource(
    override val id: Int,
    val color: Color,
    val thicknessInPx: Float,
): GameResource()