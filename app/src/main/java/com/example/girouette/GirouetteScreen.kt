/**
 * FILE: GirouetteScreen.kt
 * PURPOSE: The main UI for the application. Displays the LED preview and control inputs.
 * 
 * WHAT YOU CAN CHANGE HERE:
 * 1. UI Theme: Change colors like 'GirouetteBackground' or 'LedOrange'.
 * 2. Preview Styles: Adjust fontSize, letterSpacing, or animation durations (tween values).
 * 3. Layout: Add or remove components from the Scaffold.
 */
package com.example.girouette

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private val GirouetteBackground = Color(0xFF000000)
private val GirouetteSurface = Color(0xFF1C1C1E)
private val GirouetteText = Color(0xFFFFFFFF)
private val LedOrange = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GirouetteScreen(viewModel: GirouetteViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe error events from ViewModel
    LaunchedEffect(Unit) {
        viewModel.errorEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Girouette Control Pro", color = GirouetteText) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GirouetteSurface)
            )
        },
        containerColor = GirouetteBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LED Preview Area
            LedPreview(state = uiState)

            // Control Section (Scrollable)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModeSelector(
                    selectedMode = uiState.mode,
                    onModeSelected = { viewModel.updateMode(it) }
                )

                InputSection(
                    uiState = uiState,
                    onRoute1Changed = { viewModel.updateRoute1(it) },
                    onText1Changed = { viewModel.updateText1(it) },
                    onRoute2Changed = { viewModel.updateRoute2(it) },
                    onText2Changed = { viewModel.updateText2(it) },
                    onLine2Changed = { viewModel.updateLine2(it) }
                )

                Button(
                    onClick = { viewModel.sendCurrentMessage() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LedOrange, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("TRANSMIT TO RS232", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }

                LogSection(
                    logs = uiState.logs,
                    onClearLogs = { viewModel.clearLogs() }
                )
            }
        }
    }
}

@Composable
fun LedPreview(state: GirouetteUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .border(2.dp, Color(0xFF333333), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        shape = RoundedCornerShape(12.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val displayWidth = maxWidth

            if (state.mode == GirouetteMode.CLEAR) {
                Text("SYSTEM IDLE", color = Color(0xFF222222), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                return@BoxWithConstraints
            }

            // --- ANIMATION ENGINE ---
            val infiniteTransition = rememberInfiniteTransition(label = "led")
            
            val alpha by if (state.mode == GirouetteMode.BLINK) {
                infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 0f,
                    animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                    label = "blink"
                )
            } else {
                remember { mutableStateOf(1f) }
            }

            var activeIndex by remember { mutableIntStateOf(0) }
            val scrollAnim = remember { Animatable(1f) }
            
            LaunchedEffect(state.mode, state.route1, state.text1, state.route2, state.text2, state.line2) {
                while (true) {
                    when (state.mode) {
                        GirouetteMode.SCROLLING_TWO_SCREEN -> {
                            activeIndex = 0
                            scrollAnim.snapTo(1f)
                            scrollAnim.animateTo(0f, tween(10000, easing = LinearEasing))
                            activeIndex = 1
                            scrollAnim.snapTo(1f)
                            scrollAnim.animateTo(0f, tween(10000, easing = LinearEasing))
                        }
                        GirouetteMode.SCROLLING_ONE_SCREEN, GirouetteMode.ONE_SCREEN_ROUTE_TEXT -> {
                            activeIndex = 0
                            scrollAnim.snapTo(1f)
                            scrollAnim.animateTo(0f, tween(10000, easing = LinearEasing))
                        }
                        GirouetteMode.TWO_SCREEN_ROUTE_TEXT -> {
                            scrollAnim.snapTo(0f)
                            activeIndex = 0; delay(10000)
                            activeIndex = 1; delay(10000)
                        }
                        else -> {
                            scrollAnim.snapTo(0f); activeIndex = 0; break
                        }
                    }
                }
            }

            // --- RENDER ---
            val currentRoute = if (activeIndex == 0) state.route1 else state.route2
            val currentTextLines = when (state.mode) {
                GirouetteMode.ONE_SCREEN_2_LINES -> listOf(state.text1, state.line2)
                else -> if (activeIndex == 0) listOf(state.text1) else listOf(state.text2)
            }
            
            val isScrolling = state.mode.name.contains("SCROLLING") || state.mode == GirouetteMode.ONE_SCREEN_ROUTE_TEXT
            val hasFixedRoute = state.mode == GirouetteMode.ONE_SCREEN_ROUTE_TEXT || 
                               state.mode == GirouetteMode.TWO_SCREEN_ROUTE_TEXT || 
                               state.mode == GirouetteMode.SCROLLING_TWO_SCREEN

            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = if (isScrolling) Alignment.Start else Alignment.CenterHorizontally
                    ) {
                        currentTextLines.forEach { text ->
                            Text(
                                text = text.uppercase(),
                                color = LedOrange.copy(alpha = alpha),
                                fontSize = if (currentTextLines.size == 1) 38.sp else 24.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.offset(x = displayWidth * scrollAnim.value)
                            )
                        }
                    }
                }

                if (hasFixedRoute && currentRoute.isNotEmpty()) {
                    Surface(
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterStart).zIndex(1f)
                    ) {
                        Text(
                            text = currentRoute.uppercase(),
                            color = LedOrange.copy(alpha = alpha),
                            fontSize = 42.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .border(1.dp, Color(0xFF444444), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSelector(selectedMode: GirouetteMode, onModeSelected: (GirouetteMode) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("SELECT OPERATION MODE", color = LedOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Box {
            Surface(
                onClick = { expanded = true },
                color = GirouetteSurface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF333333), RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedMode.label, color = GirouetteText)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LedOrange)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(GirouetteSurface).fillMaxWidth(0.9f)
            ) {
                GirouetteMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(mode.label, color = GirouetteText) },
                        onClick = {
                            onModeSelected(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InputSection(
    uiState: GirouetteUiState,
    onRoute1Changed: (String) -> Unit,
    onText1Changed: (String) -> Unit,
    onRoute2Changed: (String) -> Unit,
    onText2Changed: (String) -> Unit,
    onLine2Changed: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        when (uiState.mode) {
            GirouetteMode.ONE_SCREEN_1_LINE, GirouetteMode.SCROLLING_ONE_SCREEN, GirouetteMode.BLINK -> {
                GirouetteTextField(value = uiState.text1, onValueChange = onText1Changed, label = "TEXT CONTENT")
            }
            GirouetteMode.ONE_SCREEN_2_LINES -> {
                GirouetteTextField(value = uiState.text1, onValueChange = onText1Changed, label = "LINE 1")
                GirouetteTextField(value = uiState.line2, onValueChange = onLine2Changed, label = "LINE 2")
            }
            GirouetteMode.ONE_SCREEN_ROUTE_TEXT -> {
                GirouetteTextField(value = uiState.route1, onValueChange = onRoute1Changed, label = "ROUTE NUMBER")
                GirouetteTextField(value = uiState.text1, onValueChange = onText1Changed, label = "DESTINATION TEXT")
            }
            GirouetteMode.TWO_SCREEN_ROUTE_TEXT, GirouetteMode.SCROLLING_TWO_SCREEN -> {
                Text("SCREEN 1", color = LedOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                GirouetteTextField(value = uiState.route1, onValueChange = onRoute1Changed, label = "ROUTE 1")
                GirouetteTextField(value = uiState.text1, onValueChange = onText1Changed, label = "TEXT 1")
                HorizontalDivider(color = Color(0xFF333333), modifier = Modifier.padding(vertical = 4.dp))
                Text("SCREEN 2", color = LedOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                GirouetteTextField(value = uiState.route2, onValueChange = onRoute2Changed, label = "ROUTE 2")
                GirouetteTextField(value = uiState.text2, onValueChange = onText2Changed, label = "TEXT 2")
            }
            else -> {}
        }
    }
}

@Composable
fun GirouetteTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = GirouetteText,
                unfocusedTextColor = GirouetteText,
                cursorColor = LedOrange,
                focusedBorderColor = LedOrange,
                unfocusedBorderColor = Color(0xFF333333),
                focusedContainerColor = GirouetteSurface,
                unfocusedContainerColor = GirouetteSurface
            )
        )
    }
}

@Composable
fun LogSection(logs: List<String>, onClearLogs: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SERIAL MONITOR", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onClearLogs) { Text("CLEAR", color = Color.Red, fontSize = 11.sp) }
        }
        Surface(
            color = Color(0xFF0D0D0D),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(180.dp).fillMaxWidth().border(1.dp, Color(0xFF222222), RoundedCornerShape(10.dp))
        ) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                items(logs) { log ->
                    Text(log, color = if (log.contains("OUT")) Color(0xFF00FF00) else Color.LightGray, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun GirouetteScreenPreview() {
    GirouetteScreen()
}
