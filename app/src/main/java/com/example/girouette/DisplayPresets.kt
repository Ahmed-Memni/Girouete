package com.example.girouette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * FILE: DisplayPresets.kt
 * PURPOSE: Defines the preset items for the display.
 */

data class DisplayPreset(
    val name: String,
    val mode: GirouetteMode,
    val route: String = "",
    val text1: String = "",
    val text2: String = "",
    val icon: ImageVector
)

val displayPresets = listOf(
    DisplayPreset(
        name = "Clear Display",
        mode = GirouetteMode.CLEAR,
        icon = Icons.Default.Delete
    ),
    DisplayPreset(
        name = "Out of Service",
        mode = GirouetteMode.ONE_SCREEN_1_LINE,
        text1 = "OUT OF SERVICE",
        icon = Icons.Default.Block
    ),
    DisplayPreset(
        name = "Doors Open",
        mode = GirouetteMode.ONE_SCREEN_2_LINES,
        text1 = "DOORS OPEN",
        text2 = "PLEASE WAIT",
        icon = Icons.Default.MeetingRoom
    ),
    DisplayPreset(
        name = "Route 247 Kairouan",
        mode = GirouetteMode.ONE_SCREEN_ROUTE_TEXT,
        route = "0247",
        text1 = "KAIRouAN",
        icon = Icons.Default.DirectionsBus
    ),
    DisplayPreset(
        name = "Bus Full",
        mode = GirouetteMode.BLINK,
        text1 = "BUS FULL",
        icon = Icons.Default.Warning
    ),
    DisplayPreset(
        name = "Airport Express",
        mode = GirouetteMode.ONE_SCREEN_ROUTE_TEXT,
        route = "AIRP",
        text1 = "AIRPORT EXP",
        icon = Icons.Default.FlightTakeoff
    ),
    DisplayPreset(
        name = "School Bus",
        mode = GirouetteMode.ONE_SCREEN_1_LINE,
        text1 = "SCHOOL BUS",
        icon = Icons.Default.School
    ),
    DisplayPreset(
        name = "Special Service",
        mode = GirouetteMode.ONE_SCREEN_2_LINES,
        text1 = "SPECIAL",
        text2 = "SERVICE",
        icon = Icons.Default.Star
    ),
    DisplayPreset(
        name = "Emergency",
        mode = GirouetteMode.BLINK,
        text1 = "EMERGENCY",
        icon = Icons.Default.Error
    ),
    DisplayPreset(
        name = "City Center",
        mode = GirouetteMode.SCROLLING_ONE_SCREEN,
        text1 = "CITY CENTER CIRCULAR",
        icon = Icons.Default.LocationCity
    )
)
