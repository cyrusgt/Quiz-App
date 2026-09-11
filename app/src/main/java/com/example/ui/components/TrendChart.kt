package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PerformanceTrendPoint

@Composable
fun TrendChart(
    points: List<PerformanceTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Play your first quiz to see historical trends!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val gradientColor = primaryColor.copy(alpha = 0.15f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val dotColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 36f
            val paddingRight = 24f
            val paddingTop = 20f
            val paddingBottom = 32f

            val graphWidth = width - paddingLeft - paddingRight
            val graphHeight = height - paddingTop - paddingBottom

            // Draw horizontal grid lines (0%, 50%, 100%)
            val gridLevels = listOf(0f, 0.5f, 1f)
            for (level in gridLevels) {
                val y = paddingTop + graphHeight * (1f - level)
                drawLine(
                    color = gridColor,
                    start = Offset(paddingLeft, y),
                    end = Offset(width - paddingRight, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (points.size == 1) {
                // Single point visualization
                val x = paddingLeft + graphWidth / 2f
                val pct = points[0].scorePercent.coerceIn(0, 100) / 100f
                val y = paddingTop + graphHeight * (1f - pct)

                drawCircle(
                    color = primaryColor,
                    radius = 8.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
                return@Canvas
            }

            // Multiple points: draw path & fill area
            val stepX = graphWidth / (points.size - 1)
            val path = Path()
            val fillPath = Path()

            val plottedCoords = mutableListOf<Offset>()

            points.forEachIndexed { index, point ->
                val x = paddingLeft + (index * stepX)
                val pct = point.scorePercent.coerceIn(0, 100) / 100f
                val y = paddingTop + graphHeight * (1f - pct)
                plottedCoords.add(Offset(x, y))

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, paddingTop + graphHeight)
                    fillPath.lineTo(x, y)
                } else {
                    val prev = plottedCoords[index - 1]
                    val controlX1 = prev.x + (x - prev.x) / 2
                    val controlY1 = prev.y
                    val controlX2 = prev.x + (x - prev.x) / 2
                    val controlY2 = y
                    path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                    fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                }
            }

            // Close fill path to bottom of chart
            fillPath.lineTo(plottedCoords.last().x, paddingTop + graphHeight)
            fillPath.close()

            // Draw gradient area under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    startY = paddingTop,
                    endY = paddingTop + graphHeight
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw data dots
            plottedCoords.forEachIndexed { i, coord ->
                val isPeak = points[i].scorePercent >= 80
                drawCircle(
                    color = if (isPeak) dotColor else primaryColor,
                    radius = 5.dp.toPx(),
                    center = coord
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = coord
                )
            }
        }

        // Percentage labels on the left
        Text(
            text = "100%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )
        Text(
            text = "50%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        )
        Text(
            text = "0%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 12.dp)
        )
    }
}
