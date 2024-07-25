/*
 * Copyright (C) 2024 Vishal Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vgupta98.compose_game.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.GameObject
import io.github.vgupta98.compose_game.data.RoundObject
import io.github.vgupta98.compose_game.domain.GameEngine
import io.github.vgupta98.compose_game.domain.GameEngineImpl
import io.github.vgupta98.compose_game.presentation.model.BoundaryResource
import io.github.vgupta98.compose_game.presentation.model.GameResource
import io.github.vgupta98.compose_game.presentation.model.RoundObjectResource

@Composable
internal fun GameCanvas(modifier: Modifier, onDraw: GameDrawScope.() -> Unit) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gameDrawScope = GameDrawScopeImpl(this)
        gameDrawScope.onDraw()
    }
}

/**
 * A Composable function that renders the game board using the provided game engine and resources.
 *
 * @param modifier A [Modifier] for customizing the appearance and layout of this composable.
 * @param gameEngine An instance of [GameEngine] that manages the game's objects and logic.
 * @param gameResources A list of [GameResource] instances that provide the visual representations for the game objects.
 * @param onDrawAbove A lambda function to draw anything above the game objects.
 * @param onDrawBehind A lambda function to draw anything below the game objects.
 */
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

                    is RoundObject -> {
                        require(resource is RoundObjectResource) {
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
                            radius = gameObject.radius,
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
