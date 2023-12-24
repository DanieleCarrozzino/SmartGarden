package com.example.smartgarden.objects

data class ChartObject(
    val values  : List<Float>,
    val type    : CHART_TYPE,
    val title   : String
)
enum class CHART_TYPE(private val description : String) {
    HUMIDITY("Humidity"),
    TEMPERATURE("Temperature"),
    ELSE("Else");

    override fun toString(): String {
        return description
    }
}
