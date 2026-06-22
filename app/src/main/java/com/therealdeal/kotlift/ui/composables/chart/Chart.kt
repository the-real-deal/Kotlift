package com.therealdeal.kotlift.ui.composables.chart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import kotlinx.datetime.LocalDate

@Composable
fun WeeklyActivity(
    data: List<Pair<LocalDate, Double>>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val bars = remember(data, primaryColor) {
        data.map { (date, hours) ->
            Bars(
                label = dayLabels[date.dayOfWeek.ordinal],
                values = listOf(Bars.Data(value = hours, color = SolidColor(primaryColor)))
            )
        }
    }

    ColumnChart(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        data = bars,
        barProperties = BarProperties(
            spacing = 12.dp,
            thickness = 20.dp
        ),
        labelHelperProperties = LabelHelperProperties(enabled = false),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle(MaterialTheme.colorScheme.onBackground),
            labels = data.map { (date, _) -> dayLabels[date.dayOfWeek.ordinal] }
        ),
        indicatorProperties = HorizontalIndicatorProperties(enabled = false),
        gridProperties = GridProperties(enabled = false)
    )
}