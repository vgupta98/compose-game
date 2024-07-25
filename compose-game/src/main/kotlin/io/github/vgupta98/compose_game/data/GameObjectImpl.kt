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

package io.github.vgupta98.compose_game.data

/**
 * Represents a round object in the game.
 *
 * @property id The unique identifier for the object.
 * @property initialPosition The initial position of the object.
 * @property radius The radius of the round object.
 * @property mass The mass of the round object.
 * @property initialVelocity The initial velocity of the object. Default is a zero vector.
 * @property acceleration The acceleration of the object. Default is a zero vector.
 * @property initialRotation The initial rotation of the object in degrees. Default is 0.
 * @property initialAngVelocity The initial angular velocity of the object. Default is 0.
 * @property restitution The coefficient of restitution for collisions. Default is 1.
 * @property lastCollisionTime The time of the last collision. Do not set this at your end.
 */
data class RoundObject(
    override val id: Int,
    val initialPosition: Vector2D,
    val radius: Float,
    val mass: Float,
    val initialVelocity: Vector2D = Vector.zeroVector2D,
    val acceleration: Vector2D = Vector.zeroVector2D,
    val initialRotation: Float = 0f,
    val initialAngVelocity: Float = 0f,
    val restitution: Float = 1f,
    internal val lastCollisionTime: Float = 0f
) : GameObject()

/**
 * Represents a boundary in the game.
 *
 * @property id The unique identifier for the boundary.
 * @property startPosition The start position of the boundary.
 * @property endPosition The end position of the boundary.
 * @property restitution The coefficient of restitution for collisions. Default is 1.
 */
data class Boundary(
    override val id: Int,
    val startPosition: Vector2D,
    val endPosition: Vector2D,
    val restitution: Float = 1f,
) : GameObject()