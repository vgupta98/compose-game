package io.github.vgupta98.compose_game.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.VectorPainter

internal interface GameDrawScope : DrawScope {

    fun drawRoundedObject(
        painter: VectorPainter,
        x: Float,
        y: Float,
        theta: Float,
        radius: Float,
    )

    fun drawBoundary(
        color: Color,
        thickness: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
    )
}

internal class GameDrawScopeImpl(private val drawScope: DrawScope) : GameDrawScope,
    DrawScope by drawScope {
    override fun drawRoundedObject(
        painter: VectorPainter,
        x: Float,
        y: Float,
        theta: Float,
        radius: Float,
    ) {
        with(painter) {
            translate(
                left = x - radius,
                top = y - radius
            ) {
                rotate(
                    degrees = theta,
                    pivot = Offset(radius, radius)
                ) {
                    draw(
                        size = Size(2 * radius, 2 * radius)
                    )
                }
            }
        }
    }

    override fun drawBoundary(
        color: Color,
        thickness: Float,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
    ) {
        drawLine(
            color = color,
            start = Offset(x = x1, y = y1),
            end = Offset(x = x2, y = y2),
            strokeWidth = thickness
        )
    }
}