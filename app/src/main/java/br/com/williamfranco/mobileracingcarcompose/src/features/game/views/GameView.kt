package br.com.williamfranco.mobileracingcarcompose.src.features.game.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import br.com.williamfranco.mobileracingcarcompose.src.common.constants.Constants
import br.com.williamfranco.mobileracingcarcompose.src.common.constants.Constants.CAR_MOVEMENT_SPRING_ANIMATION_STIFFNESS
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.AccelerationData
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.CarPosition.Middle
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.MovementInput
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.MovementInput.Accelerometer
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.MovementInput.SwipeGestures
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.MovementInput.TapGestures
import br.com.williamfranco.mobileracingcarcompose.src.features.game.models.RacingResourcePack
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.BackgroundState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.BlockersState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.CarState
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.GameState

@Composable
fun GameView(
    gameScore: () -> Int,
    highscore: () -> Int,
    acceleration: () -> AccelerationData,
    movementInput: () -> MovementInput,
    resourcePack: () -> RacingResourcePack,
    isDevMode: () -> Boolean,
    onGameScoreIncrease: () -> Unit,
    onResetGameScore: () -> Unit,
    onBlockerRectsDraw: (List<Rect>) -> Unit,
    onCarRectDraw: (Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val carImageDrawableBitmap = ImageBitmap.imageResource(resourcePack().carImageDrawable)
    val backgroundImageBitmap = ImageBitmap.imageResource(resourcePack().backgroundImageDrawable)
    val blockerImageBitmap = ImageBitmap.imageResource(resourcePack().blockerImageDrawable)

    // Estado do jogo observável: cada transição cria uma nova instância.
    var gameState by remember { mutableStateOf(GameState()) }

    // Incrementado no reset para recriar o cenário (fundo e bloqueadores).
    var worldKey by remember { mutableIntStateOf(0) }

    // Mantido estável: gestos capturam esta instância; o reset usa carState.reset().
    val carState = remember { CarState(image = carImageDrawableBitmap) }

    // Faixa do carro observável: é esta variável que dispara a animação lateral.
    var carLane by remember { mutableFloatStateOf(Middle.fromLeftOffsetIndex()) }

    val blockersState = remember(worldKey) {
        BlockersState(image = blockerImageBitmap)
    }
    val backgroundState = remember(worldKey) {
        BackgroundState(
            image = backgroundImageBitmap,
            onGameScoreIncrease = {
                if (gameState.isRunning())
                    onGameScoreIncrease()
            }
        )
    }

    val backgroundSpeed by remember {
        derivedStateOf {
            (gameScore() / Constants.GAME_SCORE_TO_VELOCITY_RATIO) + Constants.INITIAL_VELOCITY
        }
    }

    fun syncCarLane() {
        carLane = carState.getPosition().fromLeftOffsetIndex()
    }

    fun onResetGame() {
        onResetGameScore()
        gameState = GameState()
        carState.reset()
        carLane = Middle.fromLeftOffsetIndex()
        worldKey++
    }

    BoxWithConstraints(modifier = modifier) {
        LaunchedEffect(movementInput()) {
            if (movementInput() == Accelerometer) {
                carState.moveWithAcceleration(acceleration())
                syncCarLane()
            }
        }

        val carOffsetIndex by animateFloatAsState(
            targetValue = carLane,
            label = "car offset index",
            animationSpec = spring(stiffness = CAR_MOVEMENT_SPRING_ANIMATION_STIFFNESS)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (gameState.isRunning()) {
                        when (movementInput()) {
                            TapGestures ->
                                Modifier.detectCarPositionByPointerInput(maxWidth = maxWidth.value.toInt()) { position ->
                                    carState.moveWithTapGesture(position)
                                    syncCarLane()
                                }

                            SwipeGestures -> Modifier.detectSwipeDirection(maxWidth.value.toInt()) { swipeDirection ->
                                carState.moveWithSwipeGesture(swipeDirection)
                                syncCarLane()
                            }

                            Accelerometer -> Modifier
                        }
                    } else
                        Modifier
                )
        ) {
            GameCanvas(
                gameState = gameState,
                backgroundState = backgroundState,
                backgroundSpeed = backgroundSpeed,
                blockersState = blockersState,
                carState = carState,
                carOffsetIndex = carOffsetIndex,
                onBlockerRectsDraw = onBlockerRectsDraw,
                onCarRectDraw = onCarRectDraw,
                modifier = Modifier.fillMaxSize(),
            )

            AnimatedVisibility(
                visible = gameState.isStopped() || gameState.isPaused(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                GameStateIndicator(
                    gameState = gameState,
                    onStartClicked = { gameState = gameState.run() }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            TopInfoTexts(
                gameScore = gameScore,
                highscore = highscore,
                modifier = Modifier.fillMaxWidth()
            )
            TopActionButtons(
                gameState = gameState,
                onPauseGameState = { gameState = gameState.pause() },
                onResumeGameState = { gameState = gameState.run() },
                onResetGame = ::onResetGame,
                isDevMode = isDevMode(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
