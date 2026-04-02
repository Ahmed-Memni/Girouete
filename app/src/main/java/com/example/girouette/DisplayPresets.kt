package com.example.girouette

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * FILE: DisplayPresets.kt
 * PURPOSE: Defines the preset items. 
 * If rawBytes is null, it uses the mode logic. 
 * If rawBytes has data, it sends that exactly.
 */

data class DisplayPreset(
    val name: String,
    val mode: GirouetteMode,
    val route: String = "",
    val text1: String = "",
    val text2: String = "",
    val icon: ImageVector,
    val rawBytes: ByteArray? = null // EXACT BYTES OPTION
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
        name = "Bus Full (Blink)",
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
        name = "Emergency RAW",
        mode = GirouetteMode.BLINK,
        text1 = "EMERGENCY",
        icon = Icons.Default.Error,
        rawBytes = byteArrayOf(0x05, 0x45, 0x4D, 0x45, 0x52, 0x47, 0x45, 0x4E, 0x43, 0x59, 0x04)
    ),
    DisplayPreset(
        name = "City Center Scroll",
        mode = GirouetteMode.SCROLLING_ONE_SCREEN,
        text1 = "CITY CENTER CIRCULAR",
        icon = Icons.Default.LocationCity
    ),
    DisplayPreset(
        name = "Welcome Aboard",
        mode = GirouetteMode.SCROLLING_ONE_SCREEN,
        text1 = "WELCOME ABOARD THE CITY LINK",
        icon = Icons.Default.SentimentVerySatisfied
    ),
    DisplayPreset(
        name = "Maintenance",
        mode = GirouetteMode.ONE_SCREEN_1_LINE,
        text1 = "MAINTENANCE",
        icon = Icons.Default.Build
    ),
    DisplayPreset(
        name = "Transfer Point",
        mode = GirouetteMode.ONE_SCREEN_2_LINES,
        text1 = "TRANSFER",
        text2 = "CONNECTION",
        icon = Icons.Default.SyncAlt
    ),
//    DisplayPreset(
//        name = "Not in Service RAW",
//        mode = GirouetteMode.ONE_SCREEN_1_LINE,
//        icon = Icons.Default.NoBus,
//        rawBytes = byteArrayOf(0x05, 0x01, 0x4E, 0x4F, 0x54, 0x20, 0x49, 0x4E, 0x20, 0x53, 0x45, 0x52, 0x56, 0x49, 0x43, 0x45, 0x04)
//    ),
    DisplayPreset(
        name = "Final Stop",
        mode = GirouetteMode.ONE_SCREEN_1_LINE,
        text1 = "FINAL STOP",
        icon = Icons.Default.Flag
    ),
//    DisplayPreset(
//        name = "Custom RAW Message",
//        mode = GirouetteMode.ONE_SCREEN_1_LINE,
//        icon = Icons.Default.Code,
//        rawBytes = byteArrayOf(0x05, 0x05, 0x48, 0x45, 0x4C, 0x4C, 0x4F, 0x04)
//    )
)
