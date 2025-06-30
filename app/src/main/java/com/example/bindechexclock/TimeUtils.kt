package com.example.bindechexclock

// Top-level constant for Roman numeral lookup, similar to TimeManager.ROMANS_LOOKUP
private val ROMANS_LOOKUP = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")

/**
 * Converts an integer (0-99) to its Roman numeral representation.
 * For the purpose of this clock, numbers outside this range or negative numbers
 * are handled with specific return values ("0" for 0, "??" for invalid).
 */
fun intToRomans(i: Int): String {
    if (i < 0 || i > 99) {
        return if (i == 0) "0" else "??" // Handles 0 within the check, simplifies original logic
    }
    if (i == 0) {
        return "0"
    }
    // Kotlin's string templates make concatenation cleaner
    return "${ROMANS_LOOKUP[(i % 100) / 10 + 10]}${ROMANS_LOOKUP[i % 10]}"
}

/**
 * Formats a single time unit (hour, minute, second) into a string for a given radix.
 * Handles padding for decimal and hexadecimal values.
 */
fun formatSingleUnit(unit: Int, radix: Int): String {
    val value = unit.toString(radix) // In Kotlin, toString(radix) is available on Int
    if ((radix == 10 || radix == 16) && unit < 10 && value.length < 2) {
        // Pad for decimal single digits (0-9)
        // Pad for hex single digits (0-9), A-F are single chars but > 9
        if (radix == 10 || (radix == 16 && unit < 10)) {
            return "0$value"
        }
    }
    return value.uppercase() // Ensure hex characters are uppercase
}

/**
 * Formats a total number of seconds into H:M:S components for different radices.
 * Returns an array of strings: [binary, decimal, hex, roman].
 */
fun formatDurationStrings(totalSecondsIn: Int): Array<String> {
    var totalSeconds = totalSecondsIn
    if (totalSeconds < 0) totalSeconds = 0

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Calls the Kotlin versions of formatSingleUnit and intToRomans
    val binaryStr = "${formatSingleUnit(hours, 2)}\n${formatSingleUnit(minutes, 2)}\n${formatSingleUnit(seconds, 2)}"
    val decimalStr = "${formatSingleUnit(hours, 10)}:${formatSingleUnit(minutes, 10)}:${formatSingleUnit(seconds, 10)}"
    val hexStr = "${formatSingleUnit(hours, 16)}:${formatSingleUnit(minutes, 16)}:${formatSingleUnit(seconds, 16)}"
    val romanStr = "${intToRomans(hours)}\n${intToRomans(minutes)}\n${intToRomans(seconds)}"

    return arrayOf(binaryStr, decimalStr, hexStr, romanStr)
}

// Placeholder for other utility functions
// fun helloTimeUtils(): String {
//     return "Hello from TimeUtils.kt!"
// }
