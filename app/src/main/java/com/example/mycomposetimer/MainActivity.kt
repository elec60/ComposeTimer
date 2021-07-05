package com.example.mycomposetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MyTimer()
            }
        }
    }
}

@Composable
fun MyTimer(
    activeColor: Color = Color.Green,
    inactiveColor: Color = Color.LightGray,
    circleColor: Color = Color(0xff14D106),
    thickness: Dp = 6.dp,
    radius: Dp = 100.dp,
    totalTime: Int = 20
) {

    var ended by remember {
        mutableStateOf(false)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var currPercent by remember {
        mutableStateOf(0f)
    }

    val animatedPercentage = remember { Animatable(0f, Float.VectorConverter) }

    val scope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(radius * 2)) {

                currPercent = animatedPercentage.value

                if (currPercent == 1f){
                    ended = true
                    isPlaying = false
                    currPercent = 0f
                }

                val teta = (360 * currPercent + 90) * PI / 180f
                val x = radius.toPx() * cos(teta).toFloat()
                val y = radius.toPx() * sin(teta).toFloat()

                drawArc(
                    color = inactiveColor,
                    startAngle = 90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(thickness.toPx())
                )

                drawArc(
                    color = activeColor,
                    startAngle = 90f,
                    sweepAngle = if (!ended) 360 * currPercent else 0f,
                    useCenter = false,
                    style = Stroke(thickness.toPx())
                )


                translate(size.width / 2f, size.height / 2) {
                    drawCircle(
                        color = circleColor,
                        radius = thickness.toPx() * 1.5f,
                        center = Offset(x, y)
                    )
                }
            }

            Text(
                text = "${(totalTime * (1 - currPercent)).roundToInt()}",
                fontSize = 30.sp
            )
        }

        Button(
            modifier = Modifier
                .padding(vertical = 40.dp),
            onClick = {

                isPlaying = !isPlaying

                if (ended){
                    currPercent = 0f
                    ended = false
                    scope.launch {
                        animatedPercentage.snapTo(0f)
                    }
                }

                scope.launch(Dispatchers.Main) {

                    if (!isPlaying) {
                        animatedPercentage.stop()
                    } else {
                        animatedPercentage.animateTo(
                            1f,
                            animationSpec = tween(
                                durationMillis = (totalTime * 1000 * (1 - currPercent)).toInt(),
                                easing = LinearEasing
                            )
                        )
                    }
                }
            }) {
            Text(text = if (isPlaying) "Stop" else "Start")
        }
    }

}


