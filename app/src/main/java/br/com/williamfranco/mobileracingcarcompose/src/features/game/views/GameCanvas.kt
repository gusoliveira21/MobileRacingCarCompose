package br.com.williamfranco.mobileracingcarcompose.src.features.game.views

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope
import br.com.williamfranco.mobileracingcarcompose.src.common.constants.Constants.TICKER_ANIMATION_DURATION
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.BackgroundState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.BlockersState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.CarState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.GameState

@Composable
fun GameCanvas(
    gameState: GameState,
    backgroundState: BackgroundState,
    backgroundSpeed: Int,
    blockersState: BlockersState,
    carState: CarState,
    carOffsetIndex: Float,
    onBlockerRectsDraw: (List<Rect>) -> Unit,
    onCarRectDraw: (Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val ticker by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = TICKER_ANIMATION_DURATION, easing = LinearEasing)
        ),
        label = "ticker"
    )

    Canvas(modifier = modifier) {
        // Lê o ticker na fase de draw: a cada frame o Canvas é redesenhado
        // e o movimento do cenário/bloqueadores avança sem depender de toque.
        ticker

        drawBackground(
            gameState = gameState,
            backgroundState = backgroundState,
            backgroundSpeed = backgroundSpeed
        )

        drawBlockers(
            gameState = gameState,
            blockersState = blockersState,
            backgroundSpeed = backgroundSpeed,
            onDraw = onBlockerRectsDraw
        )

        drawCar(
            carState = carState,
            carOffsetIndex = carOffsetIndex,
            onDraw = onCarRectDraw
        )
    }
}

private fun DrawScope.drawBackground(
    gameState: GameState,
    backgroundState: BackgroundState,
    backgroundSpeed: Int
) {
    if (gameState.isRunning())
        backgroundState.move(velocity = backgroundSpeed)
    backgroundState.draw(drawScope = this)
}

private fun DrawScope.drawBlockers(
    gameState: GameState,
    blockersState: BlockersState,
    backgroundSpeed: Int,
    onDraw: (List<Rect>) -> Unit,
) {
    if (!gameState.isStopped()) {
        if (!gameState.isPaused())
            blockersState.move(velocity = backgroundSpeed)
        val rects = blockersState.draw(drawScope = this)
        onDraw(rects)
    }
}

private fun DrawScope.drawCar(
    carState: CarState,
    carOffsetIndex: Float,
    onDraw: (Rect) -> Unit
) {
    val rect = carState.draw(drawScope = this, offsetIndex = carOffsetIndex)
    onDraw(rect)
}
