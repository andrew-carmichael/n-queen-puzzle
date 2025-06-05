package ca.andrewcarmichael.angryqueens.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.andrewcarmichael.angryqueens.domain.model.Position
import ca.andrewcarmichael.angryqueens.presentation.QueensProblemIntent
import ca.andrewcarmichael.angryqueens.presentation.QueensProblemViewModel
import ca.andrewcarmichael.angryqueens.presentation.isPositionOccupied

@Composable
fun QueensProblemScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: QueensProblemViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    QueensProblem(
        onIncreaseBoardSize = { viewModel.handleIntent(QueensProblemIntent.IncreaseBoardSize) },
        onDecreaseBoardSize = { viewModel.handleIntent(QueensProblemIntent.DecreaseBoardSize) },
        onPositionClick = { row, col -> viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(row, col)) },
        state = state,
        modifier = modifier
    )
}

@Composable
private fun QueensProblem(
    onIncreaseBoardSize: () -> Unit,
    onDecreaseBoardSize: () -> Unit,
    onPositionClick: (Int, Int) -> Unit,
    state: QueensProblemViewModel.State,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ChessBoard(
            onPositionClick = onPositionClick,
            state = state,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }
}

@Composable
@SuppressLint("UnusedBoxWithConstraintsScope")
private fun ChessBoard(
    onPositionClick: (row: Int, col: Int) -> Unit,
    state: QueensProblemViewModel.State,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val maxSquareSize = minOf(maxWidth, maxHeight)
        Column(
            modifier = Modifier.size(maxSquareSize)
        ) {
            (0 until state.boardSize).forEach { row ->
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    (0 until state.boardSize).forEach { col ->
                        val isLight = ((row + col) % 2) == 0
                        ChessBoardSpace(
                            onClick = { onPositionClick(row, col) },
                            isOccupied = state.isPositionOccupied(row, col),
                            isThreatened = state.threatenedPositions.contains(Position(row, col)),
                            isLightColor = isLight,
                            modifier = Modifier.weight(1f).aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChessBoardSpace(
    onClick: () -> Unit,
    isOccupied: Boolean,
    isThreatened: Boolean,
    isLightColor: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.then(
            Modifier.clickable(
                onClickLabel = "place queen",
                onClick = onClick,
                role = Role.Button,
            )
                .background(if (isLightColor) Color(0xFFECECEC) else Color(0xFF333333))
        ),
        contentAlignment = Alignment.Center,
    ) {
        if (isThreatened || isOccupied) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = Color.Red.copy(alpha = 0.3f))
            )
        }
        if (isOccupied) {
            ChessQueen(
                isLightColor = !isLightColor,
                modifier = Modifier.fillMaxSize(0.5f),
            )
        }
    }
}

@Composable
private fun ChessQueen(
    isLightColor: Boolean,
    modifier: Modifier = Modifier,
) {
    val imageVector = if (isLightColor) lightChessQueenImageVector else darkChessQueenImageVector
    Image(
        painter = rememberVectorPainter(imageVector),
        contentDescription = "Chess Queen",
        modifier = modifier,
    )
}
