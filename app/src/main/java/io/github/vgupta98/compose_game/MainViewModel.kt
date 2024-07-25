package io.github.vgupta98.compose_game

import androidx.lifecycle.ViewModel
import io.github.vgupta98.compose_game.data.Boundary
import io.github.vgupta98.compose_game.data.RoundObject
import io.github.vgupta98.compose_game.data.Vector2D
import io.github.vgupta98.compose_game.domain.GameFactory

class MainViewModel : ViewModel() {

    val gameEngine = GameFactory.getInstance()

    init {
        gameEngine.addGameObject(
            RoundObject(
                id = 1,
                initialPosition = Vector2D(100f, 300f),
                initialVelocity = Vector2D(600f, 0f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 60f,
                initialAngVelocity = 200f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 2,
                initialPosition = Vector2D(300f, 300f),
                initialVelocity = Vector2D(-100f, 0f),
                acceleration = Vector2D(0f, 0f),
                radius = 40f,
                mass = 30f,
                initialAngVelocity = 60f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 3,
                initialPosition = Vector2D(200f, 50f),
                initialVelocity = Vector2D(10f, 400f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 30f,
                initialAngVelocity = 0f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 8,
                initialPosition = Vector2D(300f, 70f),
                initialVelocity = Vector2D(10f, -300f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 40f,
                initialAngVelocity = 0f,
            )
        )


        // boundaries
        gameEngine.addGameObject(
            Boundary(
                id = 4,
                startPosition = Vector2D(1f, 1f),
                endPosition = Vector2D(600f, 1f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 5,
                startPosition = Vector2D(600f, 1f),
                endPosition = Vector2D(600f, 600f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 6,
                startPosition = Vector2D(600f, 600f),
                endPosition = Vector2D(1f, 600f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 7,
                startPosition = Vector2D(1f, 600f),
                endPosition = Vector2D(1f, 1f),
            )
        )

        gameEngine.addGameObject(
            Boundary(
                id = 9,
                startPosition = Vector2D(200f, 200f),
                endPosition = Vector2D(400f, 400f),
            )
        )

    }
}