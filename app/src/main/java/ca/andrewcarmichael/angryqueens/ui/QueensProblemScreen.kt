package ca.andrewcarmichael.angryqueens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun QueensProblemScreenRoot(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(color = Color.Cyan))
}