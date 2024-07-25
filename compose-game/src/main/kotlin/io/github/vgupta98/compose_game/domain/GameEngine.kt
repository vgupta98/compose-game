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

package io.github.vgupta98.compose_game.domain

import io.github.vgupta98.compose_game.data.GameObject
import kotlinx.coroutines.CoroutineScope

/**
 * Interface representing the core game engine.
 * Provides methods to manage game objects, control the game loop, and handle game listeners.
 */
interface GameEngine {

    /**
     * Adds a game object to the game engine.
     * The game object must have a unique ID.
     *
     * @param gameObject The game object to be added.
     * @throws IllegalArgumentException if a game object with the same ID already exists.
     */
    fun addGameObject(gameObject: GameObject)

    /**
     * Removes a game object from the game engine based on its ID.
     *
     * @param id The ID of the game object to be removed.
     */
    fun removeGameObject(id: Int)

    /**
     * Clears all game objects from the game engine.
     */
    fun clearGameObjects()

    /**
     * Starts the game loop, initiating the animation and updates.
     *
     * @param scope The CoroutineScope within which the game loop will run. This should be passed from the composable which hosts the [GameBoard].
     */
    fun startGameLoop(scope: CoroutineScope)

    /**
     * Pauses the game loop, halting the animation and updates without resetting.
     *
     * @param scope The CoroutineScope within which the game loop will be paused. This should be passed from the composable which hosts the [GameBoard].
     */
    fun pauseGameLoop(scope: CoroutineScope)

    /**
     * Stops the game loop, halting the animation and updates, resets the state and removes all listeners. This will call [clearGameObjects] and [removeAllListeners] internally.
     *
     * @param scope The CoroutineScope within which the game loop will be stopped. This should be passed from the composable which hosts the [GameBoard].
     */
    fun stopGameLoop(scope: CoroutineScope)

    /**
     * Adds a listener to the game engine to receive game events.
     *
     * @param gameListener The listener to be added.
     */
    fun addListener(gameListener: GameListener)

    /**
     * Removes all listeners from the game engine.
     */
    fun removeAllListeners()
}
