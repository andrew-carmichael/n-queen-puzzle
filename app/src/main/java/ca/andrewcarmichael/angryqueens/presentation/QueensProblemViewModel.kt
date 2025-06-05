package ca.andrewcarmichael.angryqueens.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.andrewcarmichael.angryqueens.R.string
import ca.andrewcarmichael.angryqueens.domain.QueensProblemChessGame
import ca.andrewcarmichael.angryqueens.domain.model.Position
import ca.andrewcarmichael.angryqueens.domain.model.QueensProblemChessBoardState
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QueensProblemViewModel(
    private val queensProblemChessGame: QueensProblemChessGame = QueensProblemChessGame()
) : ViewModel(), QueensProblemIntentHandler {

    private val _uiStateFlow = MutableStateFlow<State>(State(boardSize = 0))
    val uiState = _uiStateFlow.asStateFlow()

    init {
        observeChessGameChanges()
    }

    private fun observeChessGameChanges() {
        viewModelScope.launch {
            queensProblemChessGame.boardStateFlow.collect { boardState ->
                _uiStateFlow.update { currentUiState ->
                    State(
                        boardSize = boardState.boardSize,
                        placedQueens = boardState.placedQueens.map { Position(it.row, it.col) }.toPersistentSet(),
                        threatenedPositions = boardState.threatenedPositions.map { Position(it.row, it.col) }.toPersistentSet(),
                        showThreatenedPositions = currentUiState.showThreatenedPositions,
                        threatenedQueens = boardState.threatenedQueens.map { Position(it.row, it.col) }.toPersistentSet(),
                        chatBubbleMessage = boardState.toSnarkyMessage(),
                    )
                }
            }
        }
    }

    private fun QueensProblemChessBoardState.toSnarkyMessage(): ChatBubbleMessage? {
        return when {
            // game not initialized yet
            boardSize <= 0 -> null

            // no queens have been placed
            placedQueens.isEmpty() -> ChatBubbleMessage(resId = string.n_queens_instructions, args = listOf(remainingQueensToPlace))

            // placed first queen
            placedQueens.size == 1 -> ChatBubbleMessage(
                resId = string.one_queen_snark
            )

            // too may queens
            placedQueens.size > boardSize -> ChatBubbleMessage(
                resId = string.too_many_queens,
                args = listOf(placedQueens.size, boardSize, boardSize)
            )

            // winning condition
            remainingQueensToPlace == 0 && threatenedQueens.isEmpty() -> ChatBubbleMessage(
                resId = string.victory_message,
            )

            // successfully placed more than one queen
            placedQueens.isNotEmpty() && threatenedQueens.isEmpty() -> ChatBubbleMessage(
                resId = string.n_queens_progress,
                args = listOf(placedQueens.size, boardSize)
            )

            // placed all queens but not solved
            remainingQueensToPlace == 0 && threatenedQueens.isNotEmpty() -> ChatBubbleMessage(
                resId = string.failure_message,
                args = listOf(threatenedQueens.size)
            )

            // placed some queens, threats exist
            threatenedQueens.isNotEmpty() -> ChatBubbleMessage(
                resId = string.n_queens_threats_detected,
                args = listOf(threatenedQueens.size)
            )

            else -> null
        }
    }

    override fun handleIntent(intent: QueensProblemIntent) {
        when (intent) {
            QueensProblemIntent.IncreaseBoardSize -> onIncreaseBoardSizeIntent()
            QueensProblemIntent.DecreaseBoardSize -> onDecreaseBoardSizeIntent()
            QueensProblemIntent.ToggleShowThreatenedPositions -> onToggleShowThreatenedPositions()
            QueensProblemIntent.Reset -> onReset()
            is QueensProblemIntent.ToggleQueenPlacement -> onToggleQueenPlacement(row = intent.row, col = intent.col)
        }
    }

    private fun onIncreaseBoardSizeIntent() {
        queensProblemChessGame.reset(_uiStateFlow.value.boardSize + 1)
    }

    private fun onDecreaseBoardSizeIntent() {
        queensProblemChessGame.reset((_uiStateFlow.value.boardSize - 1).coerceAtLeast(MINIMUM_BOARD_SIZE))
    }

    private fun onToggleShowThreatenedPositions() {
        _uiStateFlow.update { current ->
            current.copy(
                showThreatenedPositions = !current.showThreatenedPositions,
            )
        }
    }

    private fun onReset() {
        queensProblemChessGame.reset(boardSize = _uiStateFlow.value.boardSize)
    }

    private fun onToggleQueenPlacement(row: Int, col: Int) {
        val position = Position(row, col)
        if (queensProblemChessGame.isOccupied(position))
            queensProblemChessGame.removeQueen(position)
        else
            queensProblemChessGame.placeQueen(position)
    }

    @Immutable
    data class State(
        val boardSize: Int,
        val placedQueens: ImmutableSet<Position> = persistentSetOf(),
        val showThreatenedPositions: Boolean = false,
        val threatenedPositions: ImmutableSet<Position> = persistentSetOf(),
        val threatenedQueens: ImmutableSet<Position> = persistentSetOf(),
        val chatBubbleMessage: ChatBubbleMessage? = null,
    )

    @Immutable
    data class ChatBubbleMessage(
        val resId: Int,
        val args: List<Any> = emptyList(),
    )

    companion object {
        private const val MINIMUM_BOARD_SIZE = 4
    }
}

fun QueensProblemViewModel.State.isPositionOccupied(row: Int, col: Int): Boolean {
    return placedQueens.contains(Position(row = row, col = col))
}

sealed interface QueensProblemIntent {
    data object IncreaseBoardSize : QueensProblemIntent
    data object DecreaseBoardSize : QueensProblemIntent
    data object ToggleShowThreatenedPositions : QueensProblemIntent
    data object Reset : QueensProblemIntent
    data class ToggleQueenPlacement(val row: Int, val col: Int) : QueensProblemIntent
}

fun interface QueensProblemIntentHandler {
    fun handleIntent(intent: QueensProblemIntent)
}
