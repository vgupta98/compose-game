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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateListOf
import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.GameObject
import io.github.vgupta98.compose_game.data.RoundObject
import io.github.vgupta98.compose_game.data.Vector2D
import io.github.vgupta98.compose_game.data.times
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

class GameEngineImpl internal constructor(
    private val initialConditionsChecker: InitialConditionsChecker
) : GameEngine {

    internal val gameObjects = mutableStateListOf<GameObject>()

    private val listeners = mutableListOf<GameListener>()

    private val gameLoopTime = Animatable(0f)

    @Volatile
    var isGameLoopRunning = false
        private set

    private var loopCount = 1

    private var lastPausedTime = 0f

    private var gameJob: Job? = null

    override fun addGameObject(gameObject: GameObject) {
        require(gameObject.id !in gameObjects.map { it.id }) {
            "Id: ${gameObject.id} is already present. Please use a new id."
        }
        when(gameObject) {
            is Boundary -> initialConditionsChecker.checkBoundary(gameObject)
            is RoundObject -> initialConditionsChecker.checkRoundObject(gameObject)
        }
        gameObjects.add(gameObject)
    }

    override fun removeGameObject(id: Int) {
        gameObjects.removeIf { it.id == id }
    }

    override fun clearGameObjects() {
        gameObjects.clear()
    }

    override fun startGameLoop(scope: CoroutineScope) {
        gameJob = scope.launch {
            isGameLoopRunning = true
            while (true) {
                gameLoopTime.animateTo(
                    LOOP_TIME_INTERVAL_IN_SECONDS.toFloat() * loopCount,
                    animationSpec = tween(LOOP_TIME_INTERVAL_IN_MILLIS - (lastPausedTime * 1000).toInt(), easing = LinearEasing)
                ) {
                    // check for collision for each value of updated time
                    checkForCollisions()
                }
                lastPausedTime = 0f
                loopCount++
            }
        }
    }

    override fun pauseGameLoop(scope: CoroutineScope) {
        isGameLoopRunning = false
        lastPausedTime = gameLoopTime.value % LOOP_TIME_INTERVAL_IN_SECONDS
        gameJob?.cancel()
        gameJob = null
    }

    override fun stopGameLoop(scope: CoroutineScope) {
        isGameLoopRunning = false
        gameJob?.cancel()
        gameJob = null
        loopCount = 1

        clearGameObjects()
        removeAllListeners()
        scope.launch {
            gameLoopTime.snapTo(0f)
        }
    }

    override fun addListener(gameListener: GameListener) {
        listeners.add(gameListener)
    }

    override fun removeAllListeners() {
        listeners.clear()
    }

    internal fun getPosition(
        initialPosition: Vector2D,
        initialVelocity: Vector2D,
        acceleration: Vector2D,
        lastCollisionTime: Float
    ): Vector2D {
        // use Newton's equation of motion to calculate position when initial velocity and acceleration is given
        // s = ut + 0.5at^2
        val time = gameLoopTime.value - lastCollisionTime
        return initialPosition + (initialVelocity * time) + (0.5f * acceleration * time.pow(2))
    }

    internal fun getRotation(
        initialRotation: Float,
        initialAngVelocity: Float,
        lastCollisionTime: Float
    ): Float {
        // use Newton's equation of motion to calculate rotation when initial rotation and angular velocity is given
        // θ = ωt
        val loopTime = gameLoopTime.value
        val time = loopTime - lastCollisionTime
        val t = initialRotation + initialAngVelocity * time
        return t

    }

    private fun getVelocity(
        initialVelocity: Vector2D,
        acceleration: Vector2D,
        lastCollisionTime: Float
    ): Vector2D {
        // v = u + at
        val time = gameLoopTime.value - lastCollisionTime
        return initialVelocity + acceleration * time
    }

    // todo: optimize this function
    private fun checkForCollisions() {

        for (i in gameObjects.indices) {
            for (j in i + 1 until gameObjects.size) {
                val obj1 = gameObjects[i]
                val obj2 = gameObjects[j]

                if (obj1 is RoundObject && obj2 is RoundObject) {
                    val pos1 = getPosition(
                        initialPosition = obj1.initialPosition,
                        initialVelocity = obj1.initialVelocity,
                        acceleration = obj1.acceleration,
                        lastCollisionTime = obj1.lastCollisionTime
                    )
                    val pos2 = getPosition(
                        initialPosition = obj2.initialPosition,
                        initialVelocity = obj2.initialVelocity,
                        acceleration = obj2.acceleration,
                        lastCollisionTime = obj2.lastCollisionTime
                    )
                    val v1 = getVelocity(
                        initialVelocity = obj1.initialVelocity,
                        acceleration = obj1.acceleration,
                        lastCollisionTime = obj1.lastCollisionTime
                    )
                    val v2 = getVelocity(
                        initialVelocity = obj2.initialVelocity,
                        acceleration = obj2.acceleration,
                        lastCollisionTime = obj2.lastCollisionTime
                    )
                    val m1 = obj1.mass
                    val m2 = obj2.mass

                    // unit vector along the line of impact
                    val uVector = (pos2 - pos1).normalize()

                    // coefficient of restitution to be used
                    val e = min(obj1.restitution, obj2.restitution)

                    // parallel components of v1 and v2 along the line of impact
                    val v1Parallel = (v1.dot(uVector)) * uVector
                    val v2Parallel = (v2.dot(uVector)) * uVector


                    // check whether the collision occurred or not
                    if (
                        ((pos1 - pos2).magnitude() < obj1.radius + obj2.radius) &&
                        (v1Parallel - v2Parallel).dot(pos1 - pos2) < 0
                    ) {
                        // collision occurred
                        notifyListeners(obj1.id, obj2.id)

                        val time = gameLoopTime.value

                        // perpendicular components of v1 and v2 along the line of impact
                        val v1Perpendicular = v1 - v1Parallel
                        val v2Perpendicular = v2 - v2Parallel

                        // now, calculate the final parallel components
                        val v1FinalParallel =
                            (m1 * v1Parallel + m2 * v2Parallel + m2 * e * (v2Parallel - v1Parallel)) / (m1 + m2)
                        val v2FinalParallel =
                            (m1 * v1Parallel + m2 * v2Parallel + m1 * e * (v1Parallel - v2Parallel)) / (m1 + m2)

                        // now, the final velocities
                        val v1Final = v1FinalParallel + v1Perpendicular
                        val v2Final = v2FinalParallel + v2Perpendicular

                        // --- angular collision---

                        // moment of inertia
                        val moI1 = 0.5f * m1 * obj1.radius * obj1.radius
                        val moI2 = 0.5f * m2 * obj2.radius * obj2.radius

                        val om1 = obj1.initialAngVelocity
                        val om2 = obj2.initialAngVelocity

                        // current rotations
                        val theta1 = getRotation(
                            initialRotation = obj1.initialRotation,
                            initialAngVelocity = om1,
                            lastCollisionTime = obj1.lastCollisionTime
                        )
                        val theta2 = getRotation(
                            initialRotation = obj2.initialRotation,
                            initialAngVelocity = om2,
                            lastCollisionTime = obj2.lastCollisionTime
                        )

                        val om1Final =
                            ((moI1 - moI2) * om1) / (moI1 + moI2) + (2 * moI2 * om2) / (moI1 + moI2)
                        val om2Final =
                            ((moI2 - moI1) * om2) / (moI1 + moI2) + (2 * moI1 * om1) / (moI1 + moI2)

                        // update the velocities and position
                        updateGameObject(obj1.id, v1Final, om1Final, pos1, theta1, time)
                        updateGameObject(obj2.id, v2Final, om2Final, pos2, theta2, time)
                    }
                } else if ((obj1 is Boundary && obj2 is RoundObject) || (obj2 is Boundary && obj1 is RoundObject)) {
                    val ball = if (obj1 is RoundObject) obj1 else obj2 as RoundObject
                    val boundary = if (obj1 is Boundary) obj1 else obj2 as Boundary

                    // start and end of the boundary
                    val posStart = boundary.startPosition
                    val posEnd = boundary.endPosition
                    // vector along the boundary
                    val dVec = posEnd - posStart

                    // vector normal to boundary
                    val nVec = Vector2D(dVec.y, -dVec.x)

                    // unit vector for normal to boundary
                    val nVecUnit = nVec.normalize()

                    // velocity of the ball
                    val v = getVelocity(
                        initialVelocity = ball.initialVelocity,
                        acceleration = ball.acceleration,
                        lastCollisionTime = ball.lastCollisionTime
                    )

                    // position of the ball
                    val posBall = getPosition(
                        initialPosition = ball.initialPosition,
                        initialVelocity = ball.initialVelocity,
                        acceleration = ball.acceleration,
                        lastCollisionTime = ball.lastCollisionTime
                    )

                    // distance between boundary and ball
                    val distance = abs(dVec * (posBall - posStart)) / dVec.magnitude()

                    // projection of ball on boundary
                    val projectVec =
                        posStart + ((posBall - posStart).dot(dVec) / dVec.dot(dVec)) * dVec

                    val posStartExt = posStart - ball.radius * (dVec / dVec.magnitude())
                    val posEndExt = posEnd + ball.radius * (dVec / dVec.magnitude())

                    // check if the collision occurred or not
                    if (
                        distance < ball.radius &&
                        (v.dot(nVecUnit) * nVec.dot(posBall - posStart) < 0) &&
                        (posStartExt.dot(dVec) <= projectVec.dot(dVec) && projectVec.dot(dVec) <= posEndExt.dot(dVec))
                    ) {
                        // collision occurred
                        notifyListeners(ball.id, boundary.id)

                        val time = gameLoopTime.value - 0.01f

                        // velocity component normal to boundary
                        val vNormal = (v.dot(nVecUnit)) * nVecUnit

                        // coefficient of restitution to be used
                        val e = min(ball.restitution, boundary.restitution)

                        // velocity component along the boundary
                        val vTangent = v - vNormal

                        // final velocity after collision
                        val vFinal = vTangent - e * vNormal

                        // angular collision
                        val om = ball.initialAngVelocity
                        val theta = getRotation(
                            initialRotation = ball.initialRotation,
                            initialAngVelocity = om,
                            lastCollisionTime = ball.lastCollisionTime
                        )

                        // update the velocities and positions
                        updateGameObject(ball.id, vFinal, om, posBall, theta, time)
                    }
                }
            }
        }
    }

    private fun updateGameObject(
        id: Int,
        newVelocity: Vector2D,
        newAngVelocity: Float,
        newPosition: Vector2D,
        newRotation: Float,
        collisionTime: Float
    ) {
        gameObjects.find { it.id == id }?.apply {
            if (this is RoundObject) {
                val updatedObject =
                    this.copy(
                        initialVelocity = newVelocity,
                        initialPosition = newPosition,
                        initialAngVelocity = newAngVelocity,
                        initialRotation = newRotation,
                        lastCollisionTime = collisionTime
                    )
                gameObjects[gameObjects.indexOf(this)] = updatedObject
            }
        }
    }

    private fun notifyListeners(objectId1: Int, objectId2: Int) {
        listeners.forEach { it.onCollision(objectId1, objectId2) }
    }

    companion object {

        private const val LOOP_TIME_INTERVAL_IN_MILLIS = 10_000
        private const val LOOP_TIME_INTERVAL_IN_SECONDS = 10
    }
}