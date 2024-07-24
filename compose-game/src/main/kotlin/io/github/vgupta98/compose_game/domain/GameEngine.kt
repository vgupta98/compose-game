package io.github.vgupta98.compose_game.domain

import io.github.vgupta98.compose_game.data.GameObject
import kotlinx.coroutines.CoroutineScope

interface GameEngine {

    fun addGameObject(gameObject: GameObject)

    fun removeGameObject(id: Int)

    fun clearGameObjects()

    fun startGameLoop(scope: CoroutineScope)

    fun stopGameLoop(scope: CoroutineScope)

    fun addListener(gameListener: GameListener)

    fun removeAllListeners()
}