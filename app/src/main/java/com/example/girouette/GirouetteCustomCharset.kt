package com.example.girouette

/**
 * FILE: GirouetteCustomCharset.kt
 * PURPOSE: Maps standard characters to custom hex codes required by the hardware.
 */
object GirouetteCustomCharset {
    
    // Mapping of Char to Byte. Add your custom hex codes here.
    private val charsetMap = mapOf(
        'A' to 0x41.toByte(),
        'B' to 0x42.toByte(),
        'C' to 0x43.toByte(),
        'D' to 0x44.toByte(),
        'E' to 0x45.toByte(),
        'F' to 0x46.toByte(),
        'G' to 0x47.toByte(),
        'H' to 0x48.toByte(),
        'I' to 0x49.toByte(),
        'J' to 0x4A.toByte(),
        'K' to 0x4B.toByte(),
        'L' to 0x4C.toByte(),
        'M' to 0x4D.toByte(),
        'N' to 0x4E.toByte(),
        'O' to 0x4F.toByte(),
        'P' to 0x50.toByte(),
        'Q' to 0x51.toByte(),
        'R' to 0x52.toByte(),
        'S' to 0x53.toByte(),
        'T' to 0x54.toByte(),
        'U' to 0x55.toByte(),
        'V' to 0x56.toByte(),
        'W' to 0x57.toByte(),
        'X' to 0x58.toByte(),
        'Y' to 0x59.toByte(),
        'Z' to 0x5A.toByte(),
        '0' to 0x30.toByte(),
        '1' to 0x31.toByte(),
        '2' to 0x32.toByte(),
        '3' to 0x33.toByte(),
        '4' to 0x34.toByte(),
        '5' to 0x35.toByte(),
        '6' to 0x36.toByte(),
        '7' to 0x37.toByte(),
        '8' to 0x38.toByte(),
        '9' to 0x39.toByte(),
        ' ' to 0x20.toByte(),
        // Add more special characters or overrides here if needed
    )

    /**
     * Converts a string to a byte array using the custom mapping.
     * If a character is not in the map, it defaults to a space (0x20).
     */
    fun encode(text: String): ByteArray {
        return text.uppercase().map { char ->
            charsetMap[char] ?: 0x20.toByte() // Default to space if char not found
        }.toByteArray()
    }
}
