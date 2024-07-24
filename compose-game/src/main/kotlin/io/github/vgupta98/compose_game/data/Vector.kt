package io.github.vgupta98.compose_game.data

sealed class Vector {
    abstract fun magnitude(): Float

    companion object {
        val zeroVector1D = Vector1D(0.0f)
        val unitVector1D = Vector1D(1.0f)

        val zeroVector2D = Vector2D(0.0f, 0.0f)
        val unitVector2D = Vector2D(1.0f, 0.0f)
    }
}

data class Vector1D(val x: Float) : Vector() {
    override fun magnitude(): Float = kotlin.math.abs(x)

    operator fun plus(other: Vector1D) = Vector1D(this.x + other.x)
    operator fun minus(other: Vector1D) = Vector1D(this.x - other.x)
    operator fun times(scalar: Float) = Vector1D(this.x * scalar)
    operator fun times(other: Vector) =
        when(other) {
            is Vector1D -> Vector1D(x * other.x)
            is Vector2D -> Vector2D(x * other.x, x * other.y)
        }
    operator fun div(scalar: Float) = Vector1D(this.x / scalar)
}

data class Vector2D(val x: Float, val y: Float) : Vector() {
    override fun magnitude(): Float = kotlin.math.sqrt(x * x + y * y)

    operator fun plus(other: Vector2D) = Vector2D(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vector2D) = Vector2D(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Float) = Vector2D(this.x * scalar, this.y * scalar)
    operator fun times(other: Vector1D) = Vector2D(this.x * other.x, this.y * other.x)
    operator fun div(scalar: Float) = Vector2D(this.x / scalar, this.y / scalar)
    // Dot product
    fun dot(other: Vector2D): Float = this.x * other.x + this.y * other.y

    // cross product - in 2D, it is just the magnitude of vector formed in 3D.
    operator fun times(other: Vector2D) = this.x * other.y - this.y * other.x

    fun normalize(): Vector2D {
        val mag = magnitude()
        return if (mag > 0) Vector2D(x / mag, y / mag) else Vector2D(0f, 0f)
    }
}

operator fun Float.times(other: Vector2D) = Vector2D(this * other.x, this * other.y)
operator fun Float.times(other: Vector1D) = Vector1D(this * other.x)