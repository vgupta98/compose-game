package io.github.vgupta98.compose_game.domain

object GameFactory {

    private val initialConditionsChecker by lazy { InitialConditionsChecker() }
    fun getInstance() = GameEngineImpl(initialConditionsChecker)
}