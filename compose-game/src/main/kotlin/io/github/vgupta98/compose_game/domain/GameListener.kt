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