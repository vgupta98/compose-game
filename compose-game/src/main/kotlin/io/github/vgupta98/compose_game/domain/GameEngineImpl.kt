package io.github.vgupta98.compose_game.domain

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateListOf
import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.GameObject
import io.github.vgupta98.compose_game.data.RoundedObject
import io.github.vgupta98.compose_game.data.Vector2D
import io.github.vgupta98.compose_game.data.times
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

class GameEngineImpl internal constructor() : GameEngine {

    internal val gameObjects = mutableStateListOf<GameObject>()

    private val listeners = mutableListOf<GameListener>()

    private val gameLoopTime = Animatable(0f)

    @Volatile
    private var isGameLoopRunning = false

    private var loopCount = 1

    private var gameJob: Job? = null

    override fun addGameObject(gameObject: GameObject) {
        require(gameObject.id !in gameObjects.map { it.id }) {
            "Id: ${gameObject.id} is already present. Please use a new id."
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
            while (isGameLoopRunning) {
                gameLoopTime.animateTo(
                    10f * loopCount,
                    animationSpec = tween(10_000, easing = LinearEasing)
                ) {
                    // check for collision for each value of updated time
                    checkForCollisions()
                }
                loopCount++
            }
        }
    }

    override fun stopGameLoop(scope: CoroutineScope) {
        isGameLoopRunning = false
        gameJob?.cancel()
        gameJob = null
        loopCount = 1

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
        val time = gameLoopTime.value - lastCollisionTime
        return initialRotation + (initialAngVelocity * time)
    }

    internal fun getVelocity(
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

                if (obj1 is RoundedObject && obj2 is RoundedObject) {
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
                    val uVector = (pos2 - pos1) / (pos2 - pos1).magnitude()

                    // coefficient of restitution to be used
                    val e = min(obj1.restitution, obj2.restitution)

                    // parallel components of v1 and v2 along the line of impact
                    val v1Parallel = (v1.dot(uVector)) * uVector
                    val v2Parallel = (v2.dot(uVector)) * uVector


                    // check whether the collision occurred or not
                    if (
                        ((pos1 - pos2).magnitude() < (obj1.radius + obj2.radius).x) &&
                        (v1Parallel - v2Parallel).dot(pos1 - pos2) < 0
                    ) {
                        // collision occurred
                        notifyListeners(obj1.id, obj2.id)

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

                        // update the velocities and position
                        updateGameObject(obj1.id, v1Final, pos1, gameLoopTime.value)
                        updateGameObject(obj2.id, v2Final, pos2, gameLoopTime.value)
                    }
                }
                else if ((obj1 is Boundary && obj2 is RoundedObject) || (obj2 is Boundary && obj1 is RoundedObject)) {
                    val ball = if (obj1 is RoundedObject) obj1 else obj2 as RoundedObject
                    val boundary = if (obj1 is Boundary) obj1 else obj2 as Boundary

                    // start and end of the boundary
                    val posStart = boundary.startPosition
                    val posEnd = boundary.endPosition
                    // vector along the boundary
                    val dVec = posEnd - posStart

                    // vector normal to boundary
                    val nVec = Vector2D(dVec.y, -dVec.x)

                    // unit vector for normal to boundary
                    val nVecUnit = nVec/nVec.magnitude()

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
                    val distance = abs(dVec * (posBall - posStart))/dVec.magnitude()

                    // projection of ball on boundary
                    val projectVec = posStart + ((posBall - posStart).dot(dVec)/dVec.dot(dVec)) * dVec

                    val posStartExt = posStart - ball.radius.x * (dVec/dVec.magnitude())
                    val posEndExt = posEnd + ball.radius.x * (dVec/dVec.magnitude())

                    // check if the collision occurred or not
                    if (
                        distance < ball.radius.x  &&
                        (v.dot(nVecUnit) * nVec.dot(posBall - posStart) < 0) &&
                        (posStartExt.dot(dVec) <= projectVec.dot(dVec) && projectVec.dot(dVec) <= posEndExt.dot(dVec))
                    ) {
                        // collision occurred
                        notifyListeners(ball.id, boundary.id)
                        // velocity component normal to boundary
                        val vNormal = (v.dot(nVecUnit)) * nVecUnit

                        // coefficient of restitution to be used
                        val e = min(ball.restitution, boundary.restitution)

                        // velocity component along the boundary
                        val vTangent = v - vNormal

                        // final velocity after collision
                        val vFinal = vTangent - e * vNormal

                        // update the velocities and positions
                        updateGameObject(ball.id, vFinal, posBall, gameLoopTime.value)
                    }

                }

            }
        }
    }

    private fun updateGameObject(
        id: Int,
        newVelocity: Vector2D,
        newPosition: Vector2D,
        collisionTime: Float
    ) {
        gameObjects.find { it.id == id }?.apply {
            if (this is RoundedObject) {
                val updatedObject =
                    this.copy(
                        initialVelocity = newVelocity,
                        initialPosition = newPosition,
                        lastCollisionTime = collisionTime
                    )
                gameObjects[gameObjects.indexOf(this)] = updatedObject
            }
        }
    }

    private fun notifyListeners(objectId1: Int, objectId2: Int) {
        listeners.forEach { it.onCollision(objectId1, objectId2) }
    }
}

object GameFactory {

    fun getInstance() = GameEngineImpl()
}