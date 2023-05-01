package com.orbital.cee.view.home.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GeofenceRadiusSlider(){

}


@Composable
fun SliderSample() {
    var sliderPosition by remember { mutableStateOf(0f) }
    Text(text = sliderPosition.toString(), style = MaterialTheme.typography.body1)
    Slider(value = sliderPosition, onValueChange = { sliderPosition = it })
}

@Composable
fun StepsSliderSample() {
    var sliderPosition by remember { mutableStateOf(0f) }
    Text(text = sliderPosition.toString(), style = MaterialTheme.typography.h1)
    Slider(
        value = sliderPosition,
        onValueChange = { sliderPosition = it },
        valueRange = 0f..100f,
        onValueChangeFinished = {
            // launch some business logic update with the state you hold
            // viewModel.updateSelectedSliderValue(sliderPosition)
        },
        steps = 5,
        colors = SliderDefaults.colors(
            thumbColor = Color.Red,
            activeTrackColor = Color.Green
        )
    )
}
@Composable
@Preview
fun GeofenceRadiusSliderPreview(){
    var enabledValue by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(-0.5f) }
    var values by remember { mutableStateOf(valuesList()) }
    var steps by remember { mutableStateOf(0) }
    var valueRange: ClosedFloatingPointRange<Float>? by remember { mutableStateOf(null) }
    val onValueChangeFinished by remember { mutableStateOf({}) }
    val tutorialEnabled by remember { mutableStateOf(false) }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    SunriseSlider(
        value = value,
        onValueChange = { float: Float ->
            value = float
            onValueChangeFinished()
            value = float
        },
        valueRangeParam = valueRange,
        values = values,
        steps = steps,
        interactionSource = interactionSource,
        enabled = enabledValue,
        tutorialEnabled = tutorialEnabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = sunriseSliderColorsDefault()
    )
}
private fun valuesList() = listOf(0.14f, 0.28f, 0.42f, 0.56f, 0.70f, 0.84f, 1f)

private fun sunriseSliderColorsDefault() = SunriseSliderColors(
    thumbColor = Color(0xFF495CE8),
    thumbDisabledColor = Color(0xFF495CE8),
    inThumbColor = Color(0xFF495CE8),
    trackBrush = Brush.horizontalGradient(
        listOf(
            Color(0xFF495CE8), Color(0xFF495CE8)
        ),
        tileMode = TileMode.Clamp
    ),
    inactiveTrackColor = Color(0xFFD9D9D9),
    tickActiveColor = Color(0xFF495CE8),
    tickInactiveColor = Color(0xFFD9D9D9)
)