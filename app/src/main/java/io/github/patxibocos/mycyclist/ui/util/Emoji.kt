/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.util

import java.util.Locale

fun getCountryEmoji(countryCode: String): String {
    require(Locale.getISOCountries().contains(countryCode.uppercase()))
    // Based on https://dev.to/jorik/country-code-to-flag-emoji-a21
    val codePoints = countryCode.uppercase()
        .map { char -> 127397 + char.toString().codePointAt(0) }
        .toIntArray()
    return String(codePoints, 0, codePoints.size)
}
