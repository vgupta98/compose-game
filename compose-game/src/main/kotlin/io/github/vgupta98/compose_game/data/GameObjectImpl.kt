package io.github.vgupta98.compose_game.data

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

data class Boundary(
    override val id: Int,
    val startPosition: Vector2D,
    val endPosition: Vector2D,
    val restitution: Float = 1f,
) : GameObject()