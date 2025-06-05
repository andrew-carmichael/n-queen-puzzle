package ca.andrewcarmichael.angryqueens.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.andrewcarmichael.angryqueens.domain.QueensProblemChessGame
import ca.andrewcarmichael.angryqueens.domain.model.Position
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
                _uiStateFlow.update {
                    State(
                        boardSize = boardState.boardSize,
                        placedQueens = boardState.placedQueens.map { Position(it.row, it.col) }.toPersistentSet(),
                        threatenedPositions = boardState.threatenedPositions.map { Position(it.row, it.col) }.toPersistentSet(),
                        threatenedQueens = boardState.threatenedQueens.map { Position(it.row, it.col) }.toPersistentSet()
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: QueensProblemIntent) {
        when (intent) {
            QueensProblemIntent.IncreaseBoardSize -> onIncreaseBoardSizeIntent()
            QueensProblemIntent.DecreaseBoardSize -> onDecreaseBoardSizeIntent()
            QueensProblemIntent.Reset -> onReset()
            is QueensProblemIntent.ToggleQueenPlacement -> onToggleQueenPlacement(row = intent.row, col = intent.col)
        }
    }

    private fun onIncreaseBoardSizeIntent() {
        viewModelScope.launch {
            queensProblemChessGame.reset(_uiStateFlow.value.boardSize + 1)
        }
    }

    private fun onDecreaseBoardSizeIntent() {
        viewModelScope.launch {
            queensProblemChessGame.reset((_uiStateFlow.value.boardSize - 1).coerceAtLeast(0))
        }
    }

    private fun onReset() {
        queensProblemChessGame.reset(boardSize = _uiStateFlow.value.boardSize)
    }

    private fun onToggleQueenPlacement(row: Int, col: Int) {
        viewModelScope.launch {
            val position = Position(row, col)
            if (queensProblemChessGame.isOccupied(position))
                queensProblemChessGame.removeQueen(position)
            else
                queensProblemChessGame.placeQueen(position)
        }
    }

    @Immutable
    data class State(
        val boardSize: Int,
        val isWinningPosition: Boolean = false,
        val placedQueens: ImmutableSet<Position> = persistentSetOf(),
        val threatenedPositions: ImmutableSet<Position> = persistentSetOf(),
        val threatenedQueens: ImmutableSet<Position> = persistentSetOf(),
    ) {
        val remainingQueensToPlace: Int get() {
            return (boardSize - placedQueens.size).coerceAtLeast(0)
        }
    }
}

fun QueensProblemViewModel.State.isPositionOccupied(row: Int, col: Int): Boolean {
    return placedQueens.contains(Position(row = row, col = col))
}

fun QueensProblemViewModel.State.isPositionOccupied(position: Position): Boolean {
    return placedQueens.contains(position)
}

sealed interface QueensProblemIntent {
    data object IncreaseBoardSize : QueensProblemIntent
    data object DecreaseBoardSize : QueensProblemIntent
    data object Reset : QueensProblemIntent
    data class ToggleQueenPlacement(val row: Int, val col: Int) : QueensProblemIntent
}

fun interface QueensProblemIntentHandler {
    fun handleIntent(intent: QueensProblemIntent)
}
