package org.kwi

object AnsiColors {

    private const val ESC="\u001b"
    
    private const val BLACK="$ESC[30m"
    private const val RED="$ESC[31m"
    private const val GREEN="$ESC[32m"
    private const val BLUE="$ESC[34m"
    private const val YELLOW="$ESC[33m"
    private const val MAGENTA="$ESC[35m"
    private const val CYAN="$ESC[36m"
    private const val WHITE="$ESC[37m"

    private const val BG_BLACK="$ESC[40m"
    private const val BG_RED="$ESC[41m"
    private const val BG_GREEN="$ESC[42m"
    private const val BG_YELLOW="$ESC[43m"
    private const val BG_BLUE="$ESC[44m"
    private const val BG_MAGENTA="$ESC[45m"
    private const val BG_CYAN="$ESC[46m"
    private const val BG_WHITE="$ESC[47m"

    private const val LIGHT_BLACK="$ESC[90m"
    private const val LIGHT_RED="$ESC[91m"
    private const val LIGHT_GREEN="$ESC[92m"
    private const val LIGHT_YELLOW="$ESC[93m"
    private const val LIGHT_BLUE="$ESC[94m"
    private const val LIGHT_MAGENTA="$ESC[95m"
    private const val LIGHT_CYAN="$ESC[96m"
    private const val LIGHT_WHITE="$ESC[97m"

    private const val LIGHT_BG_BLACK="$ESC[100m"
    private const val LIGHT_BG_RED="$ESC[101m"
    private const val LIGHT_BG_GREEN="$ESC[102m"
    private const val LIGHT_BG_YELLOW="$ESC[103m"
    private const val LIGHT_BG_BLUE="$ESC[104m"
    private const val LIGHT_BG_MAGENTA="$ESC[105m"
    private const val LIGHT_BG_CYAN="$ESC[106m"
    private const val LIGHT_BG_WHITE="$ESC[107m"

    private const val BOLD="$ESC[1m"
    private const val STOP_BOLD="$ESC[21m"
    private const val UNDERLINE="$ESC[4m"
    private const val STOP_UNDERLINE="$ESC[24m"
    private const val BLINK="$ESC[5m"

    const val RESET="$ESC[0m"

    const val R=RED
    const val G=GREEN
    const val B=BLUE
    const val Y=YELLOW
    const val M=MAGENTA
    const val C=CYAN
    const val W=WHITE
    const val K=BLACK

    const val Rl=LIGHT_RED
    const val Gl=LIGHT_GREEN
    const val Bl=LIGHT_BLUE
    const val Yl=LIGHT_YELLOW
    const val Ml=LIGHT_MAGENTA
    const val Cl=LIGHT_CYAN
    const val Wl=LIGHT_WHITE

    const val bR=BG_RED
    const val bG=BG_GREEN
    const val bB=BG_BLUE
    const val bY=BG_YELLOW
    const val bM=BG_MAGENTA
    const val bC=BG_CYAN
    const val bW=BG_WHITE

    const val E=BOLD
    const val ZE=STOP_BOLD

    const val Z=RESET

    fun white(s: String): String{
        return "$Wl$s$Z"
    }

    fun grey(s: String): String{
        return "$W$s$Z"
    }

    fun black(s: String): String{
        return "$K$s$Z"
    }

    fun red(s: String): String{
        return "$R$s$Z"
    }

    fun green(s: String): String{
        return "$G$s$Z"
    }

    fun blue(s: String): String{
        return "$B$s$Z"
    }

    fun magenta(s: String): String{
        return "$M$s$Z"
    }

    fun yellow(s: String): String{
        return "$Y$s$Z"
    }

    fun cyan(s: String): String{
        return "$C$s$Z"
    }

    fun redb(s: String): String{
        return "$bR$Wl$s$Z"
    }

    fun greenb(s: String): String{
        return "$bG$Wl$s$Z"
    }

    fun blueb(s: String): String{
        return "$bB$Wl$s$Z"
    }

    fun color(color: String, s: String): String{
        return "$color$s$Z"
    }

    fun yellowb(s: String): String{
        return "$bY$K$s$Z"
    }

    fun bold(s: String): String{
        return "$BOLD$s$STOP_BOLD"
    }

    fun underline(s: String): String{
        return "$UNDERLINE$s$STOP_UNDERLINE"
    }

    fun blink(s: String): String{
        return "$BLINK$s$Z"
    }
}