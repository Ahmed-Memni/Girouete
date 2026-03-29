/**
 * FILE: GirouetteViewModel.kt
 * PURPOSE: Manages the UI state and triggers RS232 transmission.
 */
package com.example.girouette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GirouetteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GirouetteUiState())
    val uiState: StateFlow<GirouetteUiState> = _uiState.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    private val serialManager = GirouetteSerialManager()
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        val connected = serialManager.connect()
        if (!connected) {
            viewModelScope.launch {
                _errorEvents.emit("Could not open port /dev/ttyS8. Check permissions.")
            }
        }
    }

    fun updateMode(mode: GirouetteMode) {
        _uiState.update { it.copy(mode = mode) }
    }

    fun updateRoute1(text: String) = _uiState.update { it.copy(route1 = text.take(17)) }
    fun updateText1(text: String) = _uiState.update { it.copy(text1 = text.take(17)) }
    fun updateRoute2(text: String) = _uiState.update { it.copy(route2 = text.take(17)) }
    fun updateText2(text: String) = _uiState.update { it.copy(text2 = text.take(17)) }
    fun updateLine2(text: String) = _uiState.update { it.copy(line2 = text.take(17)) }

    fun sendCurrentMessage() {
        val state = _uiState.value
        viewModelScope.launch {
            // Optimized: Build once, use twice
            val messageBytes = GirouetteProtocol.buildMessage(state)
            
            // 1. Log the exact bytes being sent
            val hexString = messageBytes.joinToString(" ") { "%02X".format(it) }
            val logEntry = "[${dateFormat.format(Date())}] OUT: $hexString"
            _uiState.update { it.copy(logs = (listOf(logEntry) + it.logs).take(100)) }
            
            // 2. Send the state to manager (which handles chunking)
            val success = serialManager.sendData(state)
            if (!success) {
                _errorEvents.emit("Port failed to send. Device might be disconnected.")
            }
        }
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    override fun onCleared() {
        super.onCleared()
        serialManager.disconnect()
    }
}
