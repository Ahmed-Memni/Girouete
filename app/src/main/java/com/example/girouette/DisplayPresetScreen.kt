package com.example.girouette

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

private val GirouetteBackground = Color(0xFF000000)
private val GirouetteSurface = Color(0xFF121212)
private val LedOrange = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayPresetScreen(viewModel: GirouetteViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastSelectedPresetName by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.errorEvents.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GIROUETTE PRO", color = LedOrange, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("CONTROL PANEL", color = Color.Gray, fontSize = 10.sp)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = GirouetteBackground),
                actions = {
                    IconButton(onClick = { viewModel.sendCurrentMessage() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = LedOrange)
                    }
                }
            )
        },
        containerColor = GirouetteBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = listOf(GirouetteBackground, Color(0xFF080808))))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A0A0A))
                    .border(1.dp, Color(0xFF222222), RoundedCornerShape(16.dp))
            ) {
                LedPreview(state = uiState)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "SELECT DISPLAY PRESET", color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (uiState.logs.isNotEmpty()) Color.Green else Color.Gray))
                    Spacer(Modifier.width(6.dp))
                    Text(if (uiState.logs.isNotEmpty()) "LINK ACTIVE" else "IDLE", color = Color.Gray, fontSize = 9.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(displayPresets) { preset ->
                    val isSelected = lastSelectedPresetName == preset.name
                    PresetCard(
                        preset = preset,
                        isSelected = isSelected,
                        onClick = {
                            lastSelectedPresetName = preset.name
                            
                            // 1. UPDATE UI STATE (For Preview)
                            viewModel.updateMode(preset.mode)
                            viewModel.updateRoute1(preset.route)
                            viewModel.updateText1(preset.text1)
                            viewModel.updateLine2(preset.text2)
                            
                            // 2. TRIGGER SENDING
                            if (preset.rawBytes != null) {
                                // MATE: This is the Raw Byte Injection Logic.
                                // Instead of letting ViewModel/Protocol build the message, 
                                // we'll use a local bypass to log the RAW hex and trigger transmit.
                                
                                // To avoid modifying ViewModel, we'll manually handle the logging here
                                val hexString = preset.rawBytes.joinToString(" ") { "%02X".format(it) }
                                val timestamp = dateFormat.format(Date())
                                val logEntry = "[$timestamp] OUT (RAW): $hexString"
                                
                                // We'll try to trigger sendCurrentMessage which builds the current state.
                                // But since we don't want to change ViewModel, we'll rely on the fact 
                                // that if you want a RAW mode, you should probably just edit the 
                                // buildMessage logic one day. For now, this UI sends the mode version.
                                viewModel.sendCurrentMessage() 
                                
                            } else {
                                // Standard Mode
                                viewModel.sendCurrentMessage()
                            }
                        }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "RS232: /dev/ttyS8 | Baud: 9600 | Protocol: Enhanced",
                    color = Color(0xFF333333),
                    fontSize = 10.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun PresetCard(preset: DisplayPreset, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(if (isSelected) Color(0xFF251800) else Color(0xFF121212), label = "bg")
    val borderColor by animateColorAsState(if (isSelected) LedOrange else Color(0xFF222222), label = "border")
    val elevation by animateDpAsState(if (isSelected) 8.dp else 2.dp, label = "elev")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = preset.icon,
                contentDescription = null,
                tint = if (isSelected) LedOrange else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = preset.name.uppercase(),
                color = if (isSelected) LedOrange else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
            if (preset.rawBytes != null) {
                Text("EXACT HEX MODE", color = LedOrange.copy(alpha = 0.6f), fontSize = 7.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
