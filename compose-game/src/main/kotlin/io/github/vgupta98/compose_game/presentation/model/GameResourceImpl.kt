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