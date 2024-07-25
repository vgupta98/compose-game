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
                initialVelocity = Vector2D(300f, 40f),
                acceleration = Vector2D(30f, -10f),
                radius = 30f,
                mass = 60f,
                initialAngVelocity = 200f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 2,
                initialPosition = Vector2D(300f, 300f),
                initialVelocity = Vector2D(100f, -100f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 30f,
                initialAngVelocity = 60f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 3,
                initialPosition = Vector2D(500f, 500f),
                initialVelocity = Vector2D(30f, -300f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 30f,
                initialAngVelocity = 0f,
            )
        )

        gameEngine.addGameObject(
            RoundObject(
                id = 8,
                initialPosition = Vector2D(500f, 700f),
                initialVelocity = Vector2D(10f, 300f),
                acceleration = Vector2D(0f, 0f),
                radius = 30f,
                mass = 40f,
                initialAngVelocity = 0f,
            )
        )
        gameEngine.addGameObject(
            RoundObject(
                id = 9,
                initialPosition = Vector2D(200f, 700f),
                initialVelocity = Vector2D(100f, 200f),
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
                startPosition = Vector2D(50f, 50f),
                endPosition = Vector2D(650f, 50f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 5,
                startPosition = Vector2D(650f, 50f),
                endPosition = Vector2D(650f, 900f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 6,
                startPosition = Vector2D(650f, 900f),
                endPosition = Vector2D(50f, 900f),
            )
        )
        gameEngine.addGameObject(
            Boundary(
                id = 7,
                startPosition = Vector2D(50f, 900f),
                endPosition = Vector2D(50f, 50f),
            )
        )

    }
}