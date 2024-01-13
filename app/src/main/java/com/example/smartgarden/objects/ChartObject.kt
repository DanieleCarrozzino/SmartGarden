package com.example.smartgarden.objects

import androidx.compose.ui.graphics.Color
import com.example.smartgarden.ui.theme.Blue20
import com.example.smartgarden.ui.theme.Blue80
import com.example.smartgarden.ui.theme.Green20
import com.example.smartgarden.ui.theme.Green80
import com.example.smartgarden.ui.theme.Orange20
import com.example.smartgarden.ui.theme.Orange80

data class ChartObject(
    val values  : List<Float>,
    val type    : CHART_TYPE,
    val title   : String
)
enum class CHART_TYPE(private val description : String) {
    HUMIDITY("Humidity"),
    TEMPERATURE("Temperature"),
    SOIL_MOISTURE("soil_moisture");

    override fun toString(): String {
        return description
    }

    fun getColors() : Pair<Color, Color>{
        return when(this){
            HUMIDITY        -> Pair(Blue20, Blue80)
            TEMPERATURE     -> Pair(Orange20, Orange80)
            SOIL_MOISTURE   -> Pair(Green20, Green80)
        }
    }

    fun getSymbol() : String{
        return when(this){
            HUMIDITY        -> "%"
            TEMPERATURE     -> "℃"
            SOIL_MOISTURE   -> "%"
        }
    }
}
