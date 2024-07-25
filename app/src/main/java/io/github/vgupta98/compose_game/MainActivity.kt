package io.github.vgupta98.compose_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vgupta98.compose_game.presentation.GameBoard
import io.github.vgupta98.compose_game.presentation.model.BoundaryResource
import io.github.vgupta98.compose_game.presentation.model.RoundedObjectResource
import io.github.vgupta98.compose_game.ui.theme.ComposegameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = viewModels<MainViewModel>().value

        enableEdgeToEdge()
        setContent {
            ComposegameTheme {
                val scope = rememberCoroutineScope()

                var isPlaying by remember {
                    mutableStateOf(false)
                }
                LaunchedEffect(Unit) {
                    viewModel.gameEngine.startGameLoop(scope)
                }
                val vector = ImageVector.vectorResource(id = R.drawable.ic_ball)
                val painter = rememberVectorPainter(image = vector)
                val painter2 = rememberVectorPainter(image = vector)
                val painter3 = rememberVectorPainter(image = vector)
                val painter4 = rememberVectorPainter(image = vector)
                Box(modifier = Modifier.fillMaxSize()) {
                    GameBoard(
                        gameEngine = viewModel.gameEngine,
                        gameResources = listOf(
                            RoundedObjectResource(
                                id = 1,
                                painter = painter
                            ),
                            RoundedObjectResource(
                                id = 2,
                                painter = painter2
                            ),
                            RoundedObjectResource(
                                id = 3,
                                painter = painter3
                            ),
                            RoundedObjectResource(
                                id = 8,
                                painter = painter4
                            ),

                            // boundaries
                            BoundaryResource(
                                id = 4,
                                color = Color.Red,
                                thicknessInPx = 5f,
                            ),
                            BoundaryResource(
                                id = 5,
                                color = Color.Red,
                                thicknessInPx = 5f,
                            ),
                            BoundaryResource(
                                id = 6,
                                color = Color.Red,
                                thicknessInPx = 5f,
                            ),
                            BoundaryResource(
                                id = 7,
                                color = Color.Red,
                                thicknessInPx = 5f,
                            ),
                            BoundaryResource(
                                id = 9,
                                color = Color.Red,
                                thicknessInPx = 5f,
                            )
                        )
                    )

                    Button(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp),
                        onClick = {
                        if (isPlaying) {
                            viewModel.gameEngine.pauseGameLoop(scope)
                            isPlaying = false
                        } else {
                            viewModel.gameEngine.startGameLoop(scope)
                            isPlaying = true
                        }
                    }) {
                        Text(text = "Play/Pause")
                    }
                }
            }
        }
    }
}
