package io.github.vgupta98.compose_game.domain

/**
 * Interface for listening to game events.
 */
interface GameListener {

    /**
     * Called when a collision occurs between two game objects.
     *
     * @param objectId1 The unique identifier of the first game object involved in the collision.
     * @param objectId2 The unique identifier of the second game object involved in the collision.
     */
    fun onCollision(objectId1: Int, objectId2: Int)
}