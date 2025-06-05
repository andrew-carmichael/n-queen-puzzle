package ca.andrewcarmichael.angryqueens.domain

import ca.andrewcarmichael.angryqueens.domain.model.Position
import ca.andrewcarmichael.angryqueens.domain.model.QueensProblemChessBoardState
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * An implementation of the Chess puzzle N-Queens. The chess board is implemented as a set of
 * Positions<Row, Col>, constrained by a square board size.
 */
class QueensProblemChessGame(
    boardSize: Int = MINIMUM_BOARD_SIZE_SQUARE,
) {

    private val _boardStateFlow =
        MutableStateFlow<QueensProblemChessBoardState>(
            QueensProblemChessBoardState(
                boardSize = boardSize,
                remainingQueensToPlace = boardSize,
            )
        )
    val boardStateFlow = _boardStateFlow.asStateFlow()

    /**
     * Returns result indicating if the given position is occupied by a queen. Throws PositionOutOfBoundsException.
     */
    fun isOccupied(position: Position): Boolean {
        return with(_boardStateFlow.value) {
            if (!position.isValidForBoardSize(boardSize))
                throw PositionOutOfBoundsException()
            placedQueens.contains(position)
        }
    }

    /**
     * Place a queen at the given position on the board. PositionOutOfBoundsException is thrown if
     * position is outside of the bounds of the board. The board's state will be updated as a result.
     * Attempting to place a queen on an already occupied space will is a no-op.
     */
    fun placeQueen(position: Position) {
        _boardStateFlow.update { currentBoardState ->
            val boardSize = currentBoardState.boardSize
            if (!position.isValidForBoardSize(boardSize))
                throw PositionOutOfBoundsException()
            if (!currentBoardState.placedQueens.contains(position)) {
                computeBoardState(
                    placedQueens = currentBoardState.placedQueens.add(position),
                    boardSize = boardSize,
                )
            } else currentBoardState
        }
    }

    /**
     * Remove a queen at an existing position on the board. PositionOutOfBoundsException is thrown if
     * position is outside of the bounds of the board. The board's state will be updated as a result.
     * Attempting to remove a queen at a position that is not occupied is a no-op.
     */
    fun removeQueen(position: Position) {
        _boardStateFlow.update { currentBoardState ->
            val boardSize = currentBoardState.boardSize
            if (!position.isValidForBoardSize(boardSize))
                throw PositionOutOfBoundsException()
            if (currentBoardState.placedQueens.contains(position)) {
                val updatedQueens = currentBoardState.placedQueens.remove(position)
                computeBoardState(
                    placedQueens = updatedQueens,
                    boardSize = boardSize,
                )
            } else currentBoardState
        }
    }

    /**
     * Given a set of positioned queens, and a board size, calculate a new board state which includes
     * both a set of threatenedPositions (occupied or not), and a set of threatenedQueens (queens
     * under thread by other queens).
     */
    private fun computeBoardState(
        placedQueens: PersistentSet<Position>,
        boardSize: Int,
    ): QueensProblemChessBoardState {
        val threatenedPositions = placedQueens.flatMap { position ->
            position.getThreatenedPositions(boardSize)
        }.toPersistentSet()
        val threatenedQueens =
            placedQueens.filter { threatenedPositions.contains(it) }.toPersistentSet()
        return QueensProblemChessBoardState(
            boardSize = boardSize,
            remainingQueensToPlace = (boardSize - placedQueens.size),
            placedQueens = placedQueens,
            threatenedPositions = threatenedPositions,
            threatenedQueens = threatenedQueens,
        )
    }

    /**
     * Determine if a position is within the bounds of a board of boardSize.
     */
    private fun Position.isValidForBoardSize(boardSize: Int): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }

    /**
     * A list of vectors representing the directions of movement for a queen chess piece.
     */
    private val directions = listOf(
        -1 to 0,    // up
        -1 to 1,    // up-right
        0 to 1,     // right
        1 to 1,     // down-right
        1 to 0,     // down
        1 to -1,    // down-left
        0 to -1,    // left
        -1 to -1,   // up-left
    )

    /**
     * Calculate the set of chess board positions that are threatened by a queen at a position. This
     * will not include the position the queen is itself placed on.
     */
    private fun Position.getThreatenedPositions(boardSize: Int): Set<Position> = buildSet {
        for ((deltaRow, deltaCol) in directions) {
            var newRow = row + deltaRow
            var newCol = col + deltaCol
            while (newRow in 0 until boardSize && newCol in 0 until boardSize) {
                add(Position(newRow, newCol))
                newRow += deltaRow
                newCol += deltaCol
            }
        }
    }

    /**
     * Reset the game with a board of boardSize.
     */
    fun reset(boardSize: Int = MINIMUM_BOARD_SIZE_SQUARE) {
        _boardStateFlow.update {
            QueensProblemChessBoardState(
                boardSize = boardSize,
                remainingQueensToPlace = boardSize,
            )
        }
    }

    companion object {
        const val MINIMUM_BOARD_SIZE_SQUARE = 4
    }
}
