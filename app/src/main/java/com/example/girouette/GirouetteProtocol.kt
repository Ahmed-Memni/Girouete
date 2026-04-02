/**
 * FILE: GirouetteProtocol.kt
 * PURPOSE: Defines the byte sequence (Protocol) for each display mode.
 * 
 * WHAT YOU CAN CHANGE HERE:
 * 1. Mode Logic: Inside the 'when(state.mode)' block, you can define exactly which bytes are sent.
 * 2. Hex Values: Use 'byteArrayOf(0x05, 0x05...)' to set your unique Headers, Commands, and Footers.
 * 3. Sequence Order: Change the 'val finalSequence' line to reorder how parts are joined (e.g., header + cmd + text).
 * 4. Text Formatting: The 'formatLine' function at the bottom handles the 17-char padding (0x20).
 */
package com.example.girouette

import java.nio.charset.Charset

object GirouetteProtocol {

    /**
     * Builds the complete byte array to be sent over RS232 based on the current UI state.
     */
    fun buildMessage(state: GirouetteUiState): ByteArray {
        return when (state.mode) {
            GirouetteMode.ONE_SCREEN_1_LINE -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x44) // 'D'
                val text = formatLine(state.text1)
                val mid = byteArrayOf(0x0A) // LF
                val emptyLine = formatLine("")
                val footer = byteArrayOf(0x04) + "01580A4".toByteArray(Charset.forName("US-ASCII"))

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + text + mid + emptyLine + footer
                finalSequence
            }

            GirouetteMode.ONE_SCREEN_2_LINES -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x32) // '2'
                val t1 = formatLine(state.text1)
                val mid = byteArrayOf(0x0A)
                val t2 = formatLine(state.line2)
                val footer = byteArrayOf(0x04) + "01580A4".toByteArray(Charset.forName("US-ASCII"))

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + t1 + mid + t2 + footer
                finalSequence
            }

            GirouetteMode.CLEAR -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x43) // 'C'
                val footer = byteArrayOf(0x04)

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + footer
                finalSequence
            }

            GirouetteMode.ONE_SCREEN_ROUTE_TEXT -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x52) // 'R'
                val route = formatLine(state.route1)
                val mid = byteArrayOf(0x0A)
                val dest = formatLine(state.text1)
                val footer = byteArrayOf(0x04) + "01580A4".toByteArray(Charset.forName("US-ASCII"))

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + route + mid + dest + footer
                finalSequence
            }

            GirouetteMode.TWO_SCREEN_ROUTE_TEXT -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x44)
                val r1 = formatLine(state.route1)
                val t1 = formatLine(state.text1)
                val r2 = formatLine(state.route2)
                val t2 = formatLine(state.text2)
                val mid = byteArrayOf(0x0A)
                val footer = byteArrayOf(0x04) + "01580A4".toByteArray(Charset.forName("US-ASCII"))

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + r1 + mid + t1 + mid + r2 + mid + t2 + footer
                finalSequence
            }

            GirouetteMode.SCROLLING_ONE_SCREEN -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x53) // 'S'
                val text = formatLine(state.text1)
                val footer = byteArrayOf(0x04)

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + text + footer
                finalSequence
            }

            GirouetteMode.SCROLLING_TWO_SCREEN -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x54) // 'T'
                val r1 = formatLine(state.route1)
                val t1 = formatLine(state.text1)
                val r2 = formatLine(state.route2)
                val t2 = formatLine(state.text2)
                val mid = byteArrayOf(0x0A)
                val footer = byteArrayOf(0x04)

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + r1 + mid + t1 + mid + r2 + mid + t2 + footer
                finalSequence
            }

            GirouetteMode.BLINK -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x42) // 'B'
                val text = formatLine(state.text1)
                val footer = byteArrayOf(0x04)

                // --- CONSTRUCT THE SEQUENCE HERE ---
                val finalSequence = header + cmd + text + footer
                finalSequence
            }
        }
    }

    /**
     * Helper: Converts text to ASCII bytes and pads with spaces (0x20) to exactly 17 chars.
     */
    private fun formatLine(text: String): ByteArray {
        return text.take(17)
            .padEnd(17, ' ')
            .toByteArray(Charset.forName("US-ASCII"))
    }
}
