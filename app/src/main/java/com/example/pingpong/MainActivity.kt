package com.example.pingpong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pingpong.ui.theme.PingPongTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PingPongTheme {
                PingPongApp()
            }
        }
    }
}

@Composable
fun PingPongApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("game") { GameScreen(navController) }
        composable("gameover/{score}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
            GameOverScreen(navController, score)
        }
    }
}

//package com.example.pingpong
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectHorizontalDragGestures
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
//import kotlinx.coroutines.delay
//import androidx.compose.animation.core.Animatable
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.padding
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.unit.dp

//TODO после перезапуска игры мяч не двигается
//TODO сделать сверху и снизу границы игровой зоны
//TODO разнести по отдельным файлам логику и UI

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            PingPongGame()
//        }
//    }

//    @Composable
//    fun PingPongGame() {
//        val paddleWidth = 250f
//        val paddleHeight = 40f
//        val ballRadius = 30f
//        val paddleColor = Color.White
//        val ballColor = Color.White
//
//        // Состояние позиции ракетки
//        val paddleX = remember { mutableStateOf(0f) }
//
//        // Состояние позиции мяча
//        val ballX = remember { Animatable(0f) }
//        val ballY = remember { Animatable(0f) }
//
//        // Начальная скорость мяча
//        var ballSpeedX = remember { 10f }
//        var ballSpeedY = remember { 10f }
//
//        // Коэффициенты увеличения скорости
//        val speedIncreaseX = remember { mutableStateOf(0.05f) }  // Коэффициент для оси X
//        val speedIncreaseY = remember { mutableStateOf(0.05f) }  // Коэффициент для оси Y

//        // Состояние для размера Canvas
//        val canvasSize = remember { mutableStateOf(Size.Zero) }
//
//        // Счёт
//        val score = remember { mutableStateOf(0) }
//
//        // Состояние игры
//        val isGameOver = remember { mutableStateOf(false) }
//        val isGameStarted = remember { mutableStateOf(false) }
//
//        // Обработчик для старта игры
//        val startGame = {
//            isGameStarted.value = true
//            isGameOver.value = false
//            score.value = 0
//        }
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Black)
//                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onTap = {
//                            if (!isGameStarted.value) startGame() // Запуск игры при тапе
//                        }
//                    )
//                }
//                .pointerInput(Unit) {
//                    detectHorizontalDragGestures { _, dragAmount ->
//                        if (isGameStarted.value) {
//                            paddleX.value = (paddleX.value + dragAmount).coerceIn(
//                                -canvasSize.value.width / 2 + paddleWidth / 2,
//                                canvasSize.value.width / 2 - paddleWidth / 2
//                            )
//                        }
//                    }
//                }
//        ) {
//            if (!isGameStarted.value) {
//                Text(
//                    text = "Tap to start",
//                    color = Color.White,
//                    modifier = Modifier.align(Alignment.Center),
//                    style = MaterialTheme.typography.headlineMedium
//                )
//            } else {
//                Text(
//                    text = "Score: ${score.value}",
//                    color = Color.White,
//                    modifier = Modifier.align(Alignment.TopCenter),
//                    style = MaterialTheme.typography.headlineSmall
//                )

//                Canvas(modifier = Modifier.fillMaxSize()) {
//                    // Обновляем размер Canvas
//                    canvasSize.value = size
//
//                    // Нарисовать ракетку
//                    drawRect(
//                        color = paddleColor,
//                        topLeft = Offset(
//                            x = size.width / 2 + paddleX.value - paddleWidth / 2,
//                            y = size.height - 100f
//                        ),
//                        size = Size(paddleWidth, paddleHeight)
//                    )
//
//                    // Нарисовать мяч
//                    drawCircle(
//                        color = ballColor,
//                        center = Offset(
//                            x = size.width / 2 + ballX.value,
//                            y = size.height / 2 + ballY.value
//                        ),
//                        radius = ballRadius
//                    )
//                }
//
//                // Анимация движения мяча
//                LaunchedEffect(isGameStarted.value) {
//                    if (isGameStarted.value) {
//                        ballX.snapTo(0f) // Сброс позиции мяча
//                        ballY.snapTo(0f)
//                        ballSpeedX = 10f // Сброс скоростей
//                        ballSpeedY = 10f
//
//                        while (true) {
//                            if (isGameOver.value) break

//                            ballX.snapTo(ballX.value + ballSpeedX)
//                            ballY.snapTo(ballY.value + ballSpeedY)
//
//                            // Увеличиваем скорость мяча с течением времени
//                            ballSpeedX += speedIncreaseX.value
//                            ballSpeedY += speedIncreaseY.value
//
//                            // Обработка столкновений с границами экрана
//                            if (ballX.value + ballRadius >= canvasSize.value.width / 2 ||
//                                ballX.value - ballRadius <= -canvasSize.value.width / 2
//                            ) {
//                                ballSpeedX *= -1 // Меняем направление по X
//                            }
//                            if (ballY.value - ballRadius <= -canvasSize.value.height / 2) {
//                                ballSpeedY *= -1 // Меняем направление по Y
//                            }
//
//                            // Обработка столкновений мяча с ракеткой
//                            if (ballY.value + ballRadius >= canvasSize.value.height / 2 - 100f &&
//                                ballX.value in paddleX.value - paddleWidth / 2..paddleX.value + paddleWidth / 2
//                            ) {
//                                ballSpeedY *= -1 // Отскок от ракетки
//                                score.value += 1 // Увеличиваем счёт
//                            }
//
//                            // Проверка, если мяч вышел за нижнюю границу
//                            if (ballY.value + ballRadius >= canvasSize.value.height / 2) {
//                                isGameOver.value = true
//                                break
//                            }
//
//                            // Задержка для плавности
//                            delay(16)
//                        }
//                    }
//                }
//
//
//            }

//            // Сообщение о завершении игры
//            if (isGameOver.value) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(Color.Black) // Убедимся, что фон остается чёрным
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onTap = {
//                                    // Перезапускаем игру при тапе
//                                    isGameStarted.value = false
//                                    isGameOver.value = false
//                                    score.value = 0
//
//                                    // Сбрасываем скорости мяча
//                                    ballSpeedX = 10f
//                                    ballSpeedY = 10f
//
//                                    // Старт игры
//                                    startGame()
//                                }
//                            )
//                        },
//                    contentAlignment = Alignment.Center
//                ) {
//                    // Тексты выводятся вертикально с отступами
//                    androidx.compose.foundation.layout.Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Game Over",
//                            color = Color.White,
//                            style = MaterialTheme.typography.headlineMedium,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//                        Text(
//                            text = "Score: ${score.value}",
//                            color = Color.White,
//                            style = MaterialTheme.typography.headlineSmall,
//                            modifier = Modifier.padding(bottom = 16.dp)
//                        )
//                        Text(
//                            text = "Tap to restart",
//                            color = Color.White,
//                            style = MaterialTheme.typography.headlineSmall
//                        )
//                    }
//                }
//
//                // Сбрасываем позиции и скорости мяча в LaunchedEffect
//                LaunchedEffect(isGameStarted.value) {
//                    if (!isGameStarted.value) {
//                        ballX.snapTo(0f)
//                        ballY.snapTo(0f)
//                    } else {
//                        // Перезапускаем анимацию движения мяча
//                        ballX.animateTo(0f)
//                        ballY.animateTo(0f)
//                    }
//                }

//            }
//
//
//        }
//    }
//
//    @Preview
//    @Composable
//    fun PingPongGamePreview() {
//        PingPongGame()
//    }
//}