package br.com.williamfranco.mobileracingcarcompose.src.features.game.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state.GameState

@Composable
fun TopActionButtons(
    gameState: GameState,
    onPauseGameState: () -> Unit,
    onResumeGameState: () -> Unit,
    onResetGame: () -> Unit,
    isDevMode: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (gameState.isPaused()) {
            Button(onClick = onResumeGameState) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.PlayArrow),
                    contentDescription = "resume"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Resume")
            }
        } else {
            Button(
                onClick = onPauseGameState,
                enabled = gameState.isRunning()
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Pause),
                    contentDescription = "pause"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Pause")
            }
        }
        if (isDevMode) {
            Button(onClick = onResetGame) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Refresh),
                    contentDescription = "reset"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Reset")
            }
        }
    }
}
