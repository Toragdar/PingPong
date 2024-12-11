package com.example.pingpong

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun GameScreen(navController: NavController) {
    var score by remember { mutableStateOf(0) }
    val paddleX = remember { mutableStateOf(0f) }
    val ballX = remember { Animatable(0f) }
    val ballY = remember { Animatable(0f) }

    var ballSpeedX by remember { mutableStateOf(10f) }
    var ballSpeedY by remember { mutableStateOf(10f) }
    val speedIncreaseFactor = 0.05f

    var isGameOver by remember { mutableStateOf(false) }

    val paddleWidth = 250f
    val paddleHeight = 40f
    val ballRadius = 30f

    // Remember canvas dimensions
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    LaunchedEffect(isGameOver) {
        if (isGameOver) {
            navController.navigate("gameover/$score")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    paddleX.value = (paddleX.value + dragAmount).coerceIn(-400f, 400f)
                }
            }
    ) {
        Text(
            text = "Score: $score",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Black)
                .padding(start = 32.dp, top = 32.dp)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            // Update canvas dimensions
            canvasWidth = size.width
            canvasHeight = size.height

            val frameThickness = paddleHeight

            // Frame (left, top, right)
            drawRect(
                color = Color.White,
                topLeft = Offset(0f, 0f),
                size = Size(frameThickness, canvasHeight)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, frameThickness)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(canvasWidth - frameThickness, 0f),
                size = Size(frameThickness, canvasHeight)
            )

            // Paddle
            drawRect(
                color = Color.White,
                topLeft = Offset(
                    x = canvasWidth / 2 + paddleX.value - paddleWidth / 2,
                    y = canvasHeight - 100f
                ),
                size = Size(paddleWidth, paddleHeight)
            )

            // Ball
            drawCircle(
                color = Color.White,
                center = Offset(
                    x = canvasWidth / 2 + ballX.value,
                    y = canvasHeight / 2 + ballY.value
                ),
                radius = ballRadius
            )
        }

        LaunchedEffect(canvasWidth, canvasHeight) {
            if (canvasWidth > 0 && canvasHeight > 0) {
                ballX.snapTo(0f)
                ballY.snapTo(0f)
                ballSpeedX = 10f
                ballSpeedY = 10f

                while (!isGameOver) {
                    ballX.snapTo(ballX.value + ballSpeedX)
                    ballY.snapTo(ballY.value + ballSpeedY)

                    // Wall collisions with correction
                    if (ballX.value - ballRadius <= -canvasWidth / 2) {
                        ballX.snapTo(-canvasWidth / 2 + ballRadius + 1f)  // Добавляем небольшой запас
                        ballSpeedX *= -1
                    } else if (ballX.value + ballRadius >= canvasWidth / 2) {
                        ballX.snapTo(canvasWidth / 2 - ballRadius - 1f)  // Добавляем небольшой запас
                        ballSpeedX *= -1
                    }

                    if (ballY.value - ballRadius <= -canvasHeight / 2) {
                        ballY.snapTo(-canvasHeight / 2 + ballRadius + 1f)  // Добавляем небольшой запас
                        ballSpeedY *= -1
                    }

                    // Check for edge collision to ensure ball stays inside the bounds
                    if (ballX.value < -canvasWidth / 2 + ballRadius) {
                        ballX.snapTo(-canvasWidth / 2 + ballRadius + 1f)
                        ballSpeedX *= -1
                    } else if (ballX.value > canvasWidth / 2 - ballRadius) {
                        ballX.snapTo(canvasWidth / 2 - ballRadius - 1f)
                        ballSpeedX *= -1
                    }

                    if (ballY.value < -canvasHeight / 2 + ballRadius) {
                        ballY.snapTo(-canvasHeight / 2 + ballRadius + 1f)
                        ballSpeedY *= -1
                    }



                    // Paddle collision
                    if (
                        ballY.value + ballRadius >= canvasHeight / 2 - 100f &&
                        ballX.value in paddleX.value - paddleWidth / 2..paddleX.value + paddleWidth / 2
                    ) {
                        ballY.snapTo(canvasHeight / 2 - 100f - ballRadius) // Отталкиваем мяч от ракетки
                        ballSpeedY *= -1
                        score++

                        ballSpeedX += if (ballSpeedX > 0) speedIncreaseFactor else -speedIncreaseFactor
                        ballSpeedY += if (ballSpeedY > 0) speedIncreaseFactor else -speedIncreaseFactor
                    }

                    // Game over condition
                    if (ballY.value + ballRadius >= canvasHeight / 2) {
                        isGameOver = true
                        break
                    }

                    delay(16)
                }
            }
        }

    }
}