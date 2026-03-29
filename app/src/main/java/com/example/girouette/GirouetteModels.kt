package com.example.girouette

enum class GirouetteMode(val label: String) {
    ONE_SCREEN_1_LINE("One Screen: 1 Line"),
    ONE_SCREEN_2_LINES("One Screen: 2 Lines"),
    CLEAR("Clear"),
    ONE_SCREEN_ROUTE_TEXT("One Screen: Route + Text"),
    TWO_SCREEN_ROUTE_TEXT("Two Screen: Route + Text"),
    SCROLLING_ONE_SCREEN("Scrolling One Screen"),
    SCROLLING_TWO_SCREEN("Scrolling Two Screen"),
    BLINK("Blink")
}

data class GirouetteUiState(
    val mode: GirouetteMode = GirouetteMode.ONE_SCREEN_1_LINE,
    val route1: String = "",
    val text1: String = "",
    val route2: String = "",
    val text2: String = "",
    val line2: String = "",
    val logs: List<String> = emptyList()
)
