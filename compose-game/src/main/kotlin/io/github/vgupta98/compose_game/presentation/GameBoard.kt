package io.github.vgupta98.compose_game.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.GameObject
import io.github.vgupta98.compose_game.data.RoundedObject
import io.github.vgupta98.compose_game.domain.GameEngine
import io.github.vgupta98.compose_game.domain.GameEngineImpl
import io.github.vgupta98.compose_game.presentation.model.BoundaryResource
import io.github.vgupta98.compose_game.presentation.model.GameResource
import io.github.vgupta98.compose_game.presentation.model.RoundedObjectResource

@Composable
internal fun GameCanvas(modifier: Modifier, onDraw: GameDrawScope.() -> Unit) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gameDrawScope = GameDrawScopeImpl(this)
        gameDrawScope.onDraw()
    }
}

@Composable
fun GameBoard(
    modifier: Modifier = Modifier,
    gameEngine: GameEngine,
    gameResources: List<GameResource>,
    onDrawAbove: DrawScope.() -> Unit = {},
    onDrawBehind: DrawScope.() -> Unit = {},
) {

    Box(modifier = modifier) {
        GameCanvas(modifier = Modifier.fillMaxSize()) {
            onDrawBehind()
            (gameEngine as GameEngineImpl).gameObjects.forEach { gameObject: GameObject ->
                val resource = gameResources.find { it.id == gameObject.id }
                require(resource != null) {
                    "Resource for id: ${gameObject.id} not present."
                }

                when (gameObject) {
                    is Boundary -> {
                        require(resource is BoundaryResource) {
                            "Wrong resource type for id: ${gameObject.id}."
                        }
                        drawBoundary(
                            color = resource.color,
                            thickness = resource.thicknessInPx,
                            x1 = gameObject.startPosition.x,
                            x2 = gameObject.endPosition.x,
                            y1 = gameObject.startPosition.y,
                            y2 = gameObject.endPosition.y
                        )
                    }

                    is RoundedObject -> {
                        require(resource is RoundedObjectResource) {
                            "Wrong resource type for id: ${gameObject.id}."
                        }
                        val finalPosition = gameEngine.getPosition(
                            initialPosition = gameObject.initialPosition,
                            initialVelocity = gameObject.initialVelocity,
                            acceleration = gameObject.acceleration,
                            lastCollisionTime = gameObject.lastCollisionTime
                        )
                        drawRoundedObject(
                            painter = resource.painter,
                            radius = gameObject.radius.x,
                            theta = gameEngine.getRotation(
                                initialRotation = gameObject.initialRotation,
                                initialAngVelocity = gameObject.initialAngVelocity,
                                lastCollisionTime = gameObject.lastCollisionTime
                            ),
                            x = finalPosition.x,
                            y = finalPosition.y,
                        )
                    }
                }
            }
            onDrawAbove()
        }
    }
}
