package ca.andrewcarmichael.angryqueens.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.andrewcarmichael.angryqueens.R.string
import ca.andrewcarmichael.angryqueens.domain.model.Position
import ca.andrewcarmichael.angryqueens.presentation.QueensProblemIntent
import ca.andrewcarmichael.angryqueens.presentation.QueensProblemViewModel
import ca.andrewcarmichael.angryqueens.presentation.isPositionOccupied
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun QueensProblemScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: QueensProblemViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    QueensProblem(
        onIncreaseBoardSize = { viewModel.handleIntent(QueensProblemIntent.IncreaseBoardSize) },
        onDecreaseBoardSize = { viewModel.handleIntent(QueensProblemIntent.DecreaseBoardSize) },
        onToggleThreatenedPositions = { viewModel.handleIntent(QueensProblemIntent.ToggleShowThreatenedPositions) },
        onResign = { viewModel.handleIntent(QueensProblemIntent.Reset) },
        onPositionClick = { row, col -> viewModel.handleIntent(QueensProblemIntent.ToggleQueenPlacement(row, col)) },
        state = state,
        modifier = modifier
    )
}

@Composable
private fun QueensProblem(
    onIncreaseBoardSize: () -> Unit,
    onDecreaseBoardSize: () -> Unit,
    onToggleThreatenedPositions: () -> Unit,
    onResign: () -> Unit,
    onPositionClick: (Int, Int) -> Unit,
    state: QueensProblemViewModel.State,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        QueensProblemGameControls(
            state = state,
            onIncreaseBoardSize = onIncreaseBoardSize,
            onDecreaseBoardSize = onDecreaseBoardSize,
            onToggleThreatenedPositions = onToggleThreatenedPositions,
            onResign = onResign,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        ChessBoard(
            onPositionClick = onPositionClick,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
private fun QueensProblemGameControls(
    state: QueensProblemViewModel.State,
    onIncreaseBoardSize: () -> Unit,
    onDecreaseBoardSize: () -> Unit,
    onToggleThreatenedPositions: () -> Unit,
    onResign: () -> Unit,
    modifier: Modifier = Modifier,
    controlsExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(controlsExpanded) }
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val message = state.chatBubbleMessage?.let { chatBubbleMessage ->
                stringResource(chatBubbleMessage.resId, *chatBubbleMessage.args.toTypedArray())
            }
            ChatBubble(
                text = message.orEmpty(),
                modifier = Modifier.fillMaxWidth(),
            )
            // I got a little help from ChatGPT here with the icons and the show/hide
            TextButton(
                onClick = { expanded = !expanded }
            ) {
                val icon = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore
                Icon(icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (expanded) string.hide_controls else string.show_controls)
                    )
            }
            AnimatedVisibility(
                visible = expanded,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        IconButton(onClick = onDecreaseBoardSize) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = stringResource(string.decrease_board_size),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = state.boardSize.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        IconButton(onClick = onIncreaseBoardSize) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(string.increase_board_size),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(string.show_threatened_spaces),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Switch(
                            checked = state.showThreatenedPositions,
                            onCheckedChange = { onToggleThreatenedPositions() },
                        )
                    }
                    Button(
                        onClick = onResign,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        )
                    ) {
                        Text(text = stringResource(string.resign))
                    }
                }
            }
        }
    }
}

// ChatGPT helped me with the animation here. It didn't do a good job. I kept the use of the Surface,
// and the transitionSpec.
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ChatBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    var hasSeenFirstMessage by remember { mutableStateOf(false) }
    LaunchedEffect(text) {
        hasSeenFirstMessage = true
    }
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.padding(start = 12.dp),
    ) {
        if (!hasSeenFirstMessage) {
            ChatBubbleText(
                text = text,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    (slideInVertically { fullHeight -> fullHeight } + fadeIn()).togetherWith(
                        slideOutVertically { fullHeight -> -fullHeight } + fadeOut())
                },
                label = "ChatBubbleTextTransition"
            ) {
                ChatBubbleText(
                    text = it,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubbleText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
        contentAlignment = Alignment.Center,
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
                            isThreatened =
                                state.showThreatenedPositions && (state.threatenedPositions.contains(Position(row, col))),
                            isLightColor = isLight,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
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
            Modifier
                .clickable(
                    onClickLabel = stringResource(string.place_queen),
                    onClick = onClick,
                    role = Role.Button,
                )
                .background(if (isLightColor) Color(0xFFECECEC) else Color(0xFF333333))
        ),
        contentAlignment = Alignment.Center,
    ) {
        if (isThreatened) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Red.copy(alpha = 0.3f))
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
        contentDescription = stringResource(string.chess_queen),
        modifier = modifier,
    )
}

//
// Everything below here is ChatGPT

@Composable
@Preview
private fun PreviewChessBoard(
    @PreviewParameter(GameControlsPreviewParameterProvider::class)
    state: QueensProblemViewModel.State
) {
    MaterialTheme {
        ChessBoard(
            onPositionClick = { _, _ -> },
            state = state,
        )
    }
}

private class GameControlsPreviewParameterProvider : PreviewParameterProvider<QueensProblemViewModel.State> {
    override val values = sequenceOf(
        // Empty Board
        QueensProblemViewModel.State(
            boardSize = 5,
            placedQueens = persistentSetOf(),
            threatenedPositions = persistentSetOf(),
            threatenedQueens = persistentSetOf(),
        ),

        // Mid-Game with Safe Queens
        QueensProblemViewModel.State(
            boardSize = 6,
            placedQueens = persistentSetOf(
                Position(0, 1),
                Position(2, 3),
                Position(4, 0)
            ),
            threatenedPositions = persistentSetOf(),
            threatenedQueens = persistentSetOf(),
        ),

        // Conflicts Present
        QueensProblemViewModel.State(
            boardSize = 5,
            placedQueens = persistentSetOf(
                Position(0, 0),
                Position(1, 1),
                Position(2, 2)
            ),
            threatenedPositions = persistentSetOf(
                Position(1, 1),
                Position(2, 2)
            ),
            threatenedQueens = persistentSetOf(
                Position(0, 0),
                Position(1, 1)
            ),
        ),

        // Solved
        QueensProblemViewModel.State(
            boardSize = 4,
            placedQueens = persistentSetOf(
                Position(0, 1),
                Position(1, 3),
                Position(2, 0),
                Position(3, 2)
            ),
            threatenedPositions = persistentSetOf(),
            threatenedQueens = persistentSetOf(),
        )
    )
}

@Composable
@Preview
private fun PreviewGameControls() {
    MaterialTheme {
        QueensProblemGameControls(
            state = QueensProblemViewModel.State(
                boardSize = 5,
                placedQueens = persistentSetOf(),
                threatenedPositions = persistentSetOf(),
                threatenedQueens = persistentSetOf(),
            ),
            onIncreaseBoardSize = {},
            onDecreaseBoardSize = {},
            onToggleThreatenedPositions = {},
            onResign = {},
            modifier = Modifier,
        )
    }
}


@Composable
@Preview
private fun PreviewGameControlsExpanded() {
    MaterialTheme {
        QueensProblemGameControls(
            state = QueensProblemViewModel.State(
                boardSize = 5,
                placedQueens = persistentSetOf(),
                threatenedPositions = persistentSetOf(),
                threatenedQueens = persistentSetOf(),
            ),
            onIncreaseBoardSize = {},
            onDecreaseBoardSize = {},
            onToggleThreatenedPositions = {},
            onResign = {},
            modifier = Modifier,
            controlsExpanded = true,
        )
    }
}
