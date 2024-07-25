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

sealed class Vector {
    internal abstract fun magnitude(): Float

    companion object {
        internal val zeroVector2D = Vector2D(0.0f, 0.0f)
        internal val unitVector2D = Vector2D(1.0f, 0.0f)
    }
}

/**
 * A data class representing a 2D vector with x and y coordinates.
 *
 * @property x The x-coordinate of the vector.
 * @property y The y-coordinate of the vector.
 */
data class Vector2D(val x: Float, val y: Float) : Vector() {
    override fun magnitude(): Float = kotlin.math.sqrt(x * x + y * y)

    internal operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    internal operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    internal operator fun times(scalar: Float) = Vector2D(this.x * scalar, this.y * scalar)
    internal operator fun div(scalar: Float) = Vector2D(this.x / scalar, this.y / scalar)

    // Dot product
    internal fun dot(other: Vector2D): Float = this.x * other.x + this.y * other.y

    // cross product - in 2D, it is just the magnitude of vector formed in 3D.
    internal operator fun times(other: Vector2D) = this.x * other.y - this.y * other.x

    internal fun normalize(): Vector2D {
        val mag = magnitude()
        return if (mag > 0) Vector2D(x / mag, y / mag) else Vector2D(0f, 0f)
    }
}

internal operator fun Float.times(other: Vector2D) = Vector2D(this * other.x, this * other.y)