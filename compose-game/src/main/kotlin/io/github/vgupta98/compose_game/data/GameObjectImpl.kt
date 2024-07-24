package io.github.vgupta98.compose_game.data

data class RoundedObject(
    override val id: Int,
    val initialPosition: Vector2D,
    val radius: Vector1D,
    val mass: Float,
    val initialVelocity: Vector2D = Vector.zeroVector2D,
    val acceleration: Vector2D = Vector.zeroVector2D,
    val initialRotation: Float = 0f,
    val initialAngVelocity: Float = 0f,
    val restitution: Float = 1f,
    // todo: figure out a way to not expose lastCollision time set to client.
    internal val lastCollisionTime: Float = 0f
) : GameObject() {
    init {
        require(restitution in 0f..1f) { "Restitution must be between 0 and 1" }
        require(mass > 0f) { "Mass should be a positive value." }
        require(radius.x > 0f) { "Radius should be a positive value." }
        // todo: add more conditions here
//        require(mass * initialVelocity.magnitude() < 1000f) { "momentum for this object is too high." }
//        require(initialVelocity.magnitude() < 200f) { "initial velocity should be less than 200." }
    }
}

data class Boundary(
    override val id: Int,
    val startPosition: Vector2D,
    val endPosition: Vector2D,
    val restitution: Float = 1f,
) : GameObject() {
    init {
        require(restitution in 0f..1f) { "Restitution must be between 0 and 1" }
        require(startPosition != endPosition) { "Start and end position cannot be same." }
    }
}