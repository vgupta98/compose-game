package io.github.vgupta98.compose_game.domain

interface GameListener {

    fun onCollision(objectId1: Int, objectId2: Int)
}