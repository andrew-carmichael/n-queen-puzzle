package ca.andrewcarmichael.angryqueens.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import ca.andrewcarmichael.angryqueens.domain.model.Position
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// The game logic is not overly tested here. Instead, it's completely tested in QueensProblemChessGameTest.
class QueensProblemViewModelTest {
    private lateinit var viewModel: QueensProblemViewModel

    @Before
    fun setup() {
        viewModel = QueensProblemViewModel()
    }

    @Test
    fun `initial state should have boardSize 4`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().boardSize).isEqualTo(EXPECTED_INITIAL_BOARD_SIZE)
        }
    }

    @Test
    fun `increase board size should update boardSize`() = runTest {
        viewModel.handleIntent(QueensProblemIntent.IncreaseBoardSize)
        viewModel.uiState.test {
            assertThat(awaitItem().boardSize).isEqualTo(EXPECTED_INITIAL_BOARD_SIZE + 1)
        }
    }

    @Test
    fun `decrease board size should not go below zero`() = runTest {
        viewModel.handleIntent(QueensProblemIntent.DecreaseBoardSize)
        viewModel.uiState.test {
            assertThat(awaitItem().boardSize).isEqualTo(EXPECTED_INITIAL_BOARD_SIZE - 1)
        }
    }

    @Test
    fun `placing a queen should update placedQueens`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(0, 0))
            assertThat(awaitItem().placedQueens).contains(Position(0, 0))
        }
    }

    @Test
    fun `removing a queen should update placedQueens`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(0, 0))
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(0, 0))
            assertThat(awaitItem().placedQueens).isEmpty()
        }
    }

    @Test
    fun `resigning or resetting resets the board state`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(0, 0))
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.Reset)
            with(awaitItem()) {
                assertThat(boardSize).isEqualTo(EXPECTED_INITIAL_BOARD_SIZE)
                assertThat(placedQueens).isEmpty()
                assertThat(threatenedPositions).isEmpty()
                assertThat(threatenedQueens).isEmpty()
                assertThat(chatBubbleMessage).isNotNull()
            }
        }
    }

    @Test
    fun `chatBubbleMessage should be set when queen is placed`() = runTest {
        viewModel.uiState.test {
            skipItems(1)
            viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(0, 0))
            assertThat(awaitItem().chatBubbleMessage).isNotNull()
        }
    }

    companion object {
        private const val EXPECTED_INITIAL_BOARD_SIZE = 4
    }
}