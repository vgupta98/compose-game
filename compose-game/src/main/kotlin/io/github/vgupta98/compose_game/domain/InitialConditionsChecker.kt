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

import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.RoundObject

internal class InitialConditionsChecker {

    fun checkRoundObject(gameObject: RoundObject) {

        require(gameObject.restitution in 0f..1f) { "Restitution must be between 0 and 1" }
        require(gameObject.mass > 0f) { "Mass should be a positive value." }
        require(gameObject.radius > 0f) { "Radius should be a positive value." }
        require(gameObject.lastCollisionTime == 0f) { "Do not set the lastCollisionTime." }
        require(gameObject.mass * gameObject.initialVelocity.magnitude() < INITIAL_MAX_MOMENTUM) { "momentum: ${gameObject.mass * gameObject.initialVelocity.magnitude()} should be less than $INITIAL_MAX_MOMENTUM." }
        require(gameObject.initialVelocity.magnitude() < INITIAL_MAX_VELOCITY) { "initial velocity: ${gameObject.initialVelocity.magnitude()} should be less than $INITIAL_MAX_VELOCITY." }
        require(gameObject.acceleration.magnitude() < INITIAL_MAX_ACCELERATION) { "acceleration: ${gameObject.acceleration.magnitude()} should be less than $INITIAL_MAX_ACCELERATION." }
    }

    fun checkBoundary(gameObject: Boundary) {
        require(gameObject.restitution in 0f..1f) { "Restitution must be between 0 and 1" }
        require(gameObject.startPosition != gameObject.endPosition) { "Start and end position cannot be same." }
    }

    private companion object {

        const val INITIAL_MAX_MOMENTUM = 70_000f
        const val INITIAL_MAX_VELOCITY = 700f
        const val INITIAL_MAX_ACCELERATION = 200f
    }
}