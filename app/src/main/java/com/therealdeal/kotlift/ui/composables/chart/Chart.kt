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

@Composable
fun WeeklyActivity(
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    ColumnChart(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        data = remember(primaryColor) {
            listOf(
                Bars(label = "Lun", values = listOf(Bars.Data(value = 10.0, color = SolidColor(primaryColor)))),
                Bars(label = "Mar", values = listOf(Bars.Data(value = 22.0, color = SolidColor(primaryColor)))),
                Bars(label = "Mer", values = listOf(Bars.Data(value = 15.0, color = SolidColor(primaryColor)))),
                Bars(label = "Gio", values = listOf(Bars.Data(value = 28.0, color = SolidColor(primaryColor)))),
                Bars(label = "Ven", values = listOf(Bars.Data(value = 18.0, color = SolidColor(primaryColor)))),
                Bars(label = "Sab", values = listOf(Bars.Data(value = 35.0, color = SolidColor(primaryColor)))),
                Bars(label = "Dom", values = listOf(Bars.Data(value = 12.0, color = SolidColor(primaryColor)))),
            )
        },
        barProperties = BarProperties(
            spacing = 12.dp,
            thickness = 20.dp
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = false
        ),
        labelProperties = LabelProperties(
            enabled = true,
            textStyle = TextStyle(MaterialTheme.colorScheme.onBackground),
            labels = listOf("Lun","Mar","Mer","Gio","Ven","Sab","Dom"),
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = false
        ),
        gridProperties = GridProperties(
            enabled = false
        )
    )
}