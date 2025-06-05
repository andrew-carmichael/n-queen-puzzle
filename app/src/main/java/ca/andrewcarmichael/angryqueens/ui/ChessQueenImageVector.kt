package ca.andrewcarmichael.angryqueens.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Round
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// I did not write this code. I used a public domain SVG graphic from WikiMedia Commons
// https://commons.wikimedia.org/wiki/File:Chess_qdt45.svg
// Then I used a tool to convert that SVG into compose code.
// https://composables.com/svg-to-compose
// I modified that generated code to create dark and light queens.

val darkChessQueenImageVector by lazy {
    buildQueenImageVector(
        fillColor = Color.Black,
        strokeColor = Color.Black,
    )
}

val lightChessQueenImageVector by lazy {
    buildQueenImageVector(
        fillColor = Color.White,
        strokeColor = Color.White,
    )
}

private fun buildQueenImageVector(
    fillColor: Color,
    strokeColor: Color,
): ImageVector {
    val highlightColor = if (fillColor == Color.Black) Color.White else Color.Black
    return Builder(
        name = "ChessQdt45",
        defaultWidth = 45.0.dp,
        defaultHeight = 45.0.dp,
        viewportWidth = 45.0f,
        viewportHeight = 45.0f
    ).apply {
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Round,
            strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(9.0f, 26.0f)
            curveTo(17.5f, 24.5f, 30.0f, 24.5f, 36.0f, 26.0f)
            lineTo(38.5f, 13.5f)
            lineTo(31.0f, 25.0f)
            lineTo(30.7f, 10.9f)
            lineTo(25.5f, 24.5f)
            lineTo(22.5f, 10.0f)
            lineTo(19.5f, 24.5f)
            lineTo(14.3f, 10.9f)
            lineTo(14.0f, 25.0f)
            lineTo(6.5f, 13.5f)
            lineTo(9.0f, 26.0f)
            close()
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveToRelative(9.0f, 26.0f)
            curveToRelative(0.0f, 2.0f, 1.5f, 2.0f, 2.5f, 4.0f)
            curveToRelative(1.0f, 1.5f, 1.0f, 1.0f, 0.5f, 3.5f)
            curveToRelative(-1.5f, 1.0f, -1.0f, 2.5f, -1.0f, 2.5f)
            curveToRelative(-1.5f, 1.5f, 0.0f, 2.5f, 0.0f, 2.5f)
            curveToRelative(6.5f, 1.0f, 16.5f, 1.0f, 23.0f, 0.0f)
            curveToRelative(0.0f, 0.0f, 1.5f, -1.0f, 0.0f, -2.5f)
            curveToRelative(0.0f, 0.0f, 0.5f, -1.5f, -1.0f, -2.5f)
            curveToRelative(-0.5f, -2.5f, -0.5f, -2.0f, 0.5f, -3.5f)
            curveToRelative(1.0f, -2.0f, 2.5f, -2.0f, 2.5f, -4.0f)
            curveToRelative(-8.5f, -1.5f, -18.5f, -1.5f, -27.0f, 0.0f)
            close()
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(11.5f, 30.0f)
            curveTo(15.0f, 29.0f, 30.0f, 29.0f, 33.5f, 30.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveToRelative(12.0f, 33.5f)
            curveToRelative(6.0f, -1.0f, 15.0f, -1.0f, 21.0f, 0.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(6.0f, 12.0f)
            moveToRelative(-2.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(14.0f, 9.0f)
            moveToRelative(-2.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(22.5f, 8.0f)
            moveToRelative(-2.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(31.0f, 9.0f)
            moveToRelative(-2.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
        }
        path(fill = SolidColor(fillColor), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(39.0f, 12.0f)
            moveToRelative(-2.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, 4.0f, 0.0f)
            arcToRelative(2.0f, 2.0f, 0.0f, true, true, -4.0f, 0.0f)
        }
        path(fill = SolidColor(Color.Transparent), stroke = SolidColor(strokeColor),
            strokeLineWidth = 1.5f, strokeLineCap = Butt, strokeLineJoin = Round,
            strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(11.0f, 38.5f)
            arcTo(35.0f, 35.0f, 1.0f, false, false, 34.0f, 38.5f)
        }
        path(fill = SolidColor(Color.Transparent), stroke = SolidColor(highlightColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(11.0f, 29.0f)
            arcTo(35.0f, 35.0f, 1.0f, false, true, 34.0f, 29.0f)
        }
        path(fill = SolidColor(Color.Transparent), stroke = SolidColor(highlightColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(12.5f, 31.5f)
            lineTo(32.5f, 31.5f)
        }
        path(fill = SolidColor(Color.Transparent), stroke = SolidColor(highlightColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(11.5f, 34.5f)
            arcTo(35.0f, 35.0f, 1.0f, false, false, 33.5f, 34.5f)
        }
        path(fill = SolidColor(Color.Transparent), stroke = SolidColor(highlightColor),
            strokeLineWidth = 1.5f, strokeLineCap = StrokeCap.Companion.Round,
            strokeLineJoin = Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
            moveTo(10.5f, 37.5f)
            arcTo(35.0f, 35.0f, 1.0f, false, false, 34.5f, 37.5f)
        }
    }
        .build()
}
