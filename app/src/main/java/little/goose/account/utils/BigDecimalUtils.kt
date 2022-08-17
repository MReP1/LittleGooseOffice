package little.goose.account.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toSignString(): String {
    return if(this.signum() == 1) {
        "+${this.toPlainString()}"
    } else {
        this.toPlainString()
    }
}

fun BigDecimal.getRoundTwo(): BigDecimal {
    val bigDecimal = this.setScale(2, RoundingMode.HALF_UP)
    return if (bigDecimal.remainder(BigDecimal.ONE) == BigDecimal("0.00")) {
        bigDecimal.setScale(0, RoundingMode.HALF_UP)
    } else {
        bigDecimal
    }
}