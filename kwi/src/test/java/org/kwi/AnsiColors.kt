/*
 * Copyright (c) 2025.
 * Java Wordnet Interface Library (JWI) v2.4.0 Copyright (c) 2007-2015 Mark A. Finlayson
 * Kotlin Wordnet Interface Library (KWI) v1.0 Copyright (c) 2025 Bernard Bou
 * JWI is distributed under the terms of the Creative Commons Attribution 4.0 International Public License
 * KWI is distributed under the terms of the GPL3 License
 */

package org.kwi

object AnsiColors {

    private const val ESC = "\u001b"

    private const val BLACK = "$ESC[30m"
    private const val RED = "$ESC[31m"
    private const val GREEN = "$ESC[32m"
    private const val BLUE = "$ESC[34m"
    private const val YELLOW = "$ESC[33m"
    private const val MAGENTA = "$ESC[35m"
    private const val CYAN = "$ESC[36m"
    private const val WHITE = "$ESC[37m"

    private const val BG_BLACK = "$ESC[40m"
    private const val BG_RED = "$ESC[41m"
    private const val BG_GREEN = "$ESC[42m"
    private const val BG_YELLOW = "$ESC[43m"
    private const val BG_BLUE = "$ESC[44m"
    private const val BG_MAGENTA = "$ESC[45m"
    private const val BG_CYAN = "$ESC[46m"
    private const val BG_WHITE = "$ESC[47m"

    private const val LIGHT_BLACK = "$ESC[90m"
    private const val LIGHT_RED = "$ESC[91m"
    private const val LIGHT_GREEN = "$ESC[92m"
    private const val LIGHT_YELLOW = "$ESC[93m"
    private const val LIGHT_BLUE = "$ESC[94m"
    private const val LIGHT_MAGENTA = "$ESC[95m"
    private const val LIGHT_CYAN = "$ESC[96m"
    private const val LIGHT_WHITE = "$ESC[97m"

    private const val LIGHT_BG_BLACK = "$ESC[100m"
    private const val LIGHT_BG_RED = "$ESC[101m"
    private const val LIGHT_BG_GREEN = "$ESC[102m"
    private const val LIGHT_BG_YELLOW = "$ESC[103m"
    private const val LIGHT_BG_BLUE = "$ESC[104m"
    private const val LIGHT_BG_MAGENTA = "$ESC[105m"
    private const val LIGHT_BG_CYAN = "$ESC[106m"
    private const val LIGHT_BG_WHITE = "$ESC[107m"

    private const val BOLD = "$ESC[1m"
    private const val STOP_BOLD = "$ESC[21m"
    private const val UNDERLINE = "$ESC[4m"
    private const val STOP_UNDERLINE = "$ESC[24m"
    private const val BLINK = "$ESC[5m"

    const val RESET = "$ESC[0m"

    const val R = RED
    const val G = GREEN
    const val B = BLUE
    const val Y = YELLOW
    const val M = MAGENTA
    const val C = CYAN
    const val W = WHITE
    const val K = BLACK

    const val Rl = LIGHT_RED
    const val Gl = LIGHT_GREEN
    const val Bl = LIGHT_BLUE
    const val Yl = LIGHT_YELLOW
    const val Ml = LIGHT_MAGENTA
    const val Cl = LIGHT_CYAN
    const val Wl = LIGHT_WHITE

    const val bR = BG_RED
    const val bG = BG_GREEN
    const val bB = BG_BLUE
    const val bY = BG_YELLOW
    const val bM = BG_MAGENTA
    const val bC = BG_CYAN
    const val bW = BG_WHITE

    const val E = BOLD
    const val ZE = STOP_BOLD

    const val Z = RESET

    fun white(s: CharSequence): CharSequence {
        return "$Wl$s$Z"
    }

    fun grey(s: CharSequence): CharSequence {
        return "$W$s$Z"
    }

    fun black(s: CharSequence): CharSequence {
        return "$K$s$Z"
    }

    fun red(s: CharSequence): CharSequence {
        return "$R$s$Z"
    }

    fun green(s: CharSequence): CharSequence {
        return "$G$s$Z"
    }

    fun blue(s: CharSequence): CharSequence {
        return "$B$s$Z"
    }

    fun magenta(s: CharSequence): CharSequence {
        return "$M$s$Z"
    }

    fun yellow(s: CharSequence): CharSequence {
        return "$Y$s$Z"
    }

    fun cyan(s: CharSequence): CharSequence {
        return "$C$s$Z"
    }

    fun redb(s: CharSequence): CharSequence {
        return "$bR$Wl$s$Z"
    }

    fun greenb(s: CharSequence): CharSequence {
        return "$bG$Wl$s$Z"
    }

    fun blueb(s: CharSequence): CharSequence {
        return "$bB$Wl$s$Z"
    }

    fun color(color: CharSequence, s: CharSequence): CharSequence {
        return "$color$s$Z"
    }

    fun yellowb(s: CharSequence): CharSequence {
        return "$bY$K$s$Z"
    }

    fun bold(s: CharSequence): CharSequence {
        return "$BOLD$s$STOP_BOLD"
    }

    fun underline(s: CharSequence): CharSequence {
        return "$UNDERLINE$s$STOP_UNDERLINE"
    }

    fun blink(s: CharSequence): CharSequence {
        return "$BLINK$s$Z"
    }
}