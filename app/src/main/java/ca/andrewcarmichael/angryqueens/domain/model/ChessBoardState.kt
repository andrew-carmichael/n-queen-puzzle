package ca.andrewcarmichael.angryqueens.domain.model

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

data class QueensProblemChessBoardState(
    val boardSize: Int,
    val remainingQueensToPlace: Int,
    val placedQueens: PersistentSet<Position> = persistentSetOf(),
    val threatenedPositions: PersistentSet<Position> = persistentSetOf(),
    val threatenedQueens: PersistentSet<Position> = persistentSetOf(),
) {
    val isWinningPosition: Boolean
        get() {
            return placedQueens.size == boardSize && threatenedQueens.isEmpty()
        }
}
