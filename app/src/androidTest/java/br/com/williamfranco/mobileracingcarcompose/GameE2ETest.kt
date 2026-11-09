package br.com.williamfranco.mobileracingcarcompose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameE2ETest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesGameAndShowsStoppedStateWithStartButton() {
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Game is STOPPED", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Start!").assertIsDisplayed()
        composeRule.onNodeWithText("Pause").assertIsDisplayed()
    }
}
