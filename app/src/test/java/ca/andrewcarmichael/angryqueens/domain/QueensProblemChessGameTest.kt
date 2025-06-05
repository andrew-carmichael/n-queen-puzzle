package ca.andrewcarmichael.angryqueens.domain

import app.cash.turbine.test
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import ca.andrewcarmichael.angryqueens.domain.model.Position
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class QueensProblemChessGameTest {

    @Test
    fun `verify initial state`() = runTest {
        QueensProblemChessGame().boardStateFlow.test {
            with (awaitItem()) {
                assertThat(boardSize).isEqualTo(QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE)
                assertThat(remainingQueensToPlace).isEqualTo(QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE)
                assertThat(threatenedPositions).isEmpty()
                assertThat(threatenedQueens).isEmpty()
            }
        }
    }

    @Test
    fun `verify reset() resets the game state`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            assertThat(awaitItem().placedQueens).isEmpty()
            game.placeQueen(Position(row = 0, col = 0))
            assertThat(awaitItem().placedQueens).isEqualTo(persistentSetOf(Position(row = 0, col = 0)))
            game.reset()
            with (awaitItem()) {
                assertThat(boardSize).isEqualTo(QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE)
                assertThat(remainingQueensToPlace).isEqualTo(QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE)
                assertThat(threatenedPositions).isEmpty()
                assertThat(threatenedQueens).isEmpty()
            }
        }
    }

    @Test
    fun `verify isOccupied() returns true only when a position is occupied`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            // skip initial emission
            skipItems(1)
            val firstQueen = Position(row = 0, col = 0)
            game.placeQueen(position = firstQueen)
            assertThat(awaitItem().placedQueens).containsOnly(firstQueen)
            assertThat(game.isOccupied(firstQueen)).isTrue()
            for (row in 0 until QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE) {
                for (col in 0 until QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE) {
                    val position = Position(row = row, col = col)
                    if (position == firstQueen)
                        continue
                    assertThat(game.isOccupied(position)).isFalse()
                }
            }
        }
    }

    @Test
    fun `verify isOccupied throws PositionOutOfBounds exception`()  = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            assertFailure {
                game.isOccupied(Position(row = Int.MAX_VALUE, col = Int.MIN_VALUE))
            }.isInstanceOf(PositionOutOfBoundsException::class)
        }
    }

    @Test
    fun `verify placeQueen throws PositionOutOfBounds exception`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            assertFailure {
                game.placeQueen(Position(row = Int.MAX_VALUE, col = Int.MIN_VALUE))
            }.isInstanceOf(PositionOutOfBoundsException::class)
        }
    }

    @Test
    fun `verify placeQueen adds a queen to placedQueens`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            val position = Position(row = 1, col = 1)
            game.placeQueen(position = position)
            with (awaitItem()) {
                assertThat(placedQueens).containsOnly(position)
                assertThat(remainingQueensToPlace).isEqualTo(QueensProblemChessGame.MINIMUM_BOARD_SIZE_SQUARE - 1)
                assertThat(threatenedPositions).isNotEmpty()
                assertThat(threatenedQueens).isEmpty()
            }
        }
    }

    @Test
    fun `verify placing queen in already occupied position is a no-op`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            val position = Position(row = 1, col = 1)
            game.placeQueen(position)
            skipItems(1)
            game.placeQueen(position)
            expectNoEvents()
        }
    }

    @Test
    // I used AI to generate expectedThreatenedPositions
    fun `verify placing queen updates threatened positions correctly`() = runTest {
        val boardSize = 5
        val queenPosition = Position(row = 2, col = 2)
        val expectedThreatenedPositions = buildSet {
            for (i in 0 until boardSize) {
                if (i != queenPosition.col) add(Position(queenPosition.row, i)) // same row
                if (i != queenPosition.row) add(Position(i, queenPosition.col)) // same column
            }
            // Diagonal: top-left to bottom-right
            for (i in 1 until boardSize) {
                val upRight = Position(queenPosition.row - i, queenPosition.col + i)
                val downLeft = Position(queenPosition.row + i, queenPosition.col - i)
                if (upRight.row in 0 until boardSize && upRight.col in 0 until boardSize) add(upRight)
                if (downLeft.row in 0 until boardSize && downLeft.col in 0 until boardSize) add(downLeft)
            }
            // Diagonal: top-right to bottom-left
            for (i in 1 until boardSize) {
                val upLeft = Position(queenPosition.row - i, queenPosition.col - i)
                val downRight = Position(queenPosition.row + i, queenPosition.col + i)
                if (upLeft.row in 0 until boardSize && upLeft.col in 0 until boardSize) add(upLeft)
                if (downRight.row in 0 until boardSize && downRight.col in 0 until boardSize) add(downRight)
            }
        }
        val game = QueensProblemChessGame(boardSize = boardSize)
        game.boardStateFlow.test {
            skipItems(1)
            game.placeQueen(queenPosition)
            with (awaitItem()) {
                assertThat(this.placedQueens).containsOnly(queenPosition)
                assertThat(this.threatenedQueens).isEmpty()
                assertThat(this.threatenedPositions).equals(expectedThreatenedPositions)
            }
        }
    }

    @Test
    fun `verify placing queen updates threatened queens correctly`() = runTest {
        val boardSize = 5
        val centerPosition = Position(row = 2, col = 2)
        val game = QueensProblemChessGame(boardSize = boardSize)
        game.boardStateFlow.test {
            skipItems(1)
            game.placeQueen(centerPosition)
            with (awaitItem()) {
                assertThat(placedQueens).containsOnly(centerPosition)
                assertThat(threatenedQueens).isEmpty()
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 1)
            }
            val cornerQueen = Position(row = 0, col = 0)
            game.placeQueen(cornerQueen)
            with (awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(threatenedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 2)
            }
            val unthreatningQueen = Position(row = 1, col = 4)
            game.placeQueen(unthreatningQueen)
            with (awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition, cornerQueen, unthreatningQueen))
                assertThat(threatenedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 3)
            }
        }
    }

    @Test
    fun `verify removeQueen throws PositionOutOfBoundsException`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            assertFailure {
                game.removeQueen(Position(row = Int.MAX_VALUE, col = Int.MIN_VALUE))
            }.isInstanceOf(PositionOutOfBoundsException::class)
        }
    }

    @Test
    fun `verify removeQueen removes a queen in an existing position`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            val position = Position(row = 1, col = 1)
            game.placeQueen(position)
            skipItems(1)
            game.removeQueen(position)
            with (awaitItem()) {
                assertThat(this.placedQueens).isEmpty()
                assertThat(this.threatenedPositions).isEmpty()
                assertThat(this.threatenedQueens).isEmpty()
            }
        }
    }

    @Test
    fun `verify removeQueen is a no-op for positions that are not occupied`() = runTest {
        val game = QueensProblemChessGame()
        game.boardStateFlow.test {
            skipItems(1)
            val position = Position(row = 1, col = 1)
            game.placeQueen(position)
            skipItems(1)
            game.removeQueen(position)
            with (awaitItem()) {
                assertThat(this.placedQueens).isEmpty()
                assertThat(this.threatenedPositions).isEmpty()
                assertThat(this.threatenedQueens).isEmpty()
            }
            game.removeQueen(position)
            expectNoEvents()
        }
    }


    @Test
    fun `verify removeQueen updates threatened queens correctly`() = runTest {
        val boardSize = 5
        val centerPosition = Position(row = 2, col = 2)
        val game = QueensProblemChessGame(boardSize = boardSize)
        game.boardStateFlow.test {
            skipItems(1)
            game.placeQueen(centerPosition)
            with (awaitItem()) {
                assertThat(placedQueens).containsOnly(centerPosition)
                assertThat(threatenedQueens).isEmpty()
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 1)
            }
            val cornerQueen = Position(row = 0, col = 0)
            game.placeQueen(cornerQueen)
            with (awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(threatenedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 2)
            }
            val unthreatningQueen = Position(row = 1, col = 4)
            game.placeQueen(unthreatningQueen)
            with (awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition, cornerQueen, unthreatningQueen))
                assertThat(threatenedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 3)
            }
            game.removeQueen(unthreatningQueen)
            with(awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(threatenedQueens).isEqualTo(setOf(centerPosition, cornerQueen))
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 2)
            }
            game.removeQueen(cornerQueen)
            with(awaitItem()) {
                assertThat(placedQueens).isEqualTo(setOf(centerPosition))
                assertThat(threatenedQueens).isEmpty()
                assertThat(remainingQueensToPlace).isEqualTo(boardSize - 1)
            }
        }
    }

    @Test
    fun `verify winning condition`() = runTest {
        val boardSize = 5
        val game = QueensProblemChessGame(boardSize)
        game.boardStateFlow.test {
            skipItems(1)
            game.placeQueen(Position(row = 4, col = 0))
            assertThat(awaitItem().isWinningPosition).isFalse()
            game.placeQueen(Position(row = 2, col = 1))
            assertThat(awaitItem().isWinningPosition).isFalse()
            game.placeQueen(Position(row = 0, col = 2))
            assertThat(awaitItem().isWinningPosition).isFalse()
            game.placeQueen(Position(row = 3, col = 3))
            assertThat(awaitItem().isWinningPosition).isFalse()
            game.placeQueen(Position(row = 1, col = 4))
            assertThat(awaitItem().isWinningPosition).isTrue()
            game.placeQueen(Position(row = 4, col = 4))
            assertThat(awaitItem().isWinningPosition).isFalse()
        }
    }
}
