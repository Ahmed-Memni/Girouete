/**
 * FILE: GirouetteProtocol.kt
 * PURPOSE: Defines the byte sequence (Protocol) for each display mode using custom charset.
 */
package com.example.girouette

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
                val footer = byteArrayOf(0x04) + GirouetteCustomCharset.encode("01580A4")

                header + cmd + text + mid + emptyLine + footer
            }

            GirouetteMode.ONE_SCREEN_2_LINES -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x32) // '2'
                val t1 = formatLine(state.text1)
                val mid = byteArrayOf(0x0A)
                val t2 = formatLine(state.line2)
                val footer = byteArrayOf(0x04) + GirouetteCustomCharset.encode("01580A4")

                header + cmd + t1 + mid + t2 + footer
            }

            GirouetteMode.CLEAR -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x43) // 'C'
                val footer = byteArrayOf(0x04)

                header + cmd + footer
            }

            GirouetteMode.ONE_SCREEN_ROUTE_TEXT -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x52) // 'R'
                val route = formatLine(state.route1)
                val mid = byteArrayOf(0x0A)
                val dest = formatLine(state.text1)
                val footer = byteArrayOf(0x04) + GirouetteCustomCharset.encode("01580A4")

                header + cmd + route + mid + dest + footer
            }

            GirouetteMode.TWO_SCREEN_ROUTE_TEXT -> {
                val header = byteArrayOf(0x05, 0x05, 0x41, 0x17, 0x4B, 0x41, 0x17, 0x58, 0x30, 0x17)
                val cmd = byteArrayOf(0x44)
                val r1 = formatLine(state.route1)
                val t1 = formatLine(state.text1)
                val r2 = formatLine(state.route2)
                val t2 = formatLine(state.text2)
                val mid = byteArrayOf(0x0A)
                val footer = byteArrayOf(0x04) + GirouetteCustomCharset.encode("01580A4")

                header + cmd + r1 + mid + t1 + mid + r2 + mid + t2 + footer
            }

            GirouetteMode.SCROLLING_ONE_SCREEN -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x53) // 'S'
                val text = formatLine(state.text1)
                val footer = byteArrayOf(0x04)

                header + cmd + text + footer
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

                header + cmd + r1 + mid + t1 + mid + r2 + mid + t2 + footer
            }

            GirouetteMode.BLINK -> {
                val header = byteArrayOf(0x05, 0x01)
                val cmd = byteArrayOf(0x42) // 'B'
                val text = formatLine(state.text1)
                val footer = byteArrayOf(0x04)

                header + cmd + text + footer
            }
        }
    }

    /**
     * Helper: Pads text and converts to bytes using custom charset.
     */
    private fun formatLine(text: String): ByteArray {
        val paddedText = text.take(17).padEnd(17, ' ')
        return GirouetteCustomCharset.encode(paddedText)
    }
}
