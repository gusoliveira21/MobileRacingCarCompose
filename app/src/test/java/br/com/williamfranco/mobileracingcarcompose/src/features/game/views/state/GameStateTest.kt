package br.com.williamfranco.mobileracingcarcompose.src.features.game.views.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateTest {

    @Test
    fun defaultStatus_isStopped() {
        val gameState = GameState()

        assertTrue(gameState.isStopped())
        assertFalse(gameState.isRunning())
        assertFalse(gameState.isPaused())
        assertEquals("STOPPED", gameState.getStatusName())
    }

    @Test
    fun run_returnsRunningState() {
        val gameState = GameState().run()

        assertTrue(gameState.isRunning())
        assertEquals("RUNNING", gameState.getStatusName())
    }

    @Test
    fun pause_returnsPausedState() {
        val gameState = GameState().run().pause()

        assertTrue(gameState.isPaused())
        assertEquals("PAUSED", gameState.getStatusName())
    }

    @Test
    fun resumeAfterPause_returnsRunningState() {
        val gameState = GameState().run().pause().run()

        assertTrue(gameState.isRunning())
    }

    @Test
    fun stop_returnsStoppedState() {
        val gameState = GameState().run().stop()

        assertTrue(gameState.isStopped())
        assertEquals("STOPPED", gameState.getStatusName())
    }

    @Test
    fun transitions_doNotMutateOriginalState() {
        val original = GameState()

        original.run()

        assertTrue(original.isStopped())
    }
}
