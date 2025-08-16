package co.mbznetwork.android.base.extension

import android.text.Editable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

fun Double.toCentString(n: Int) = (this * 100.0).roundToDigit(n).toInt().toString()
fun Double.toCentInt(): Int = (this * 100.0).roundTo2DecimalPlaces().toInt()

fun Double.roundToDigit(n: Int): Double = if (this.isNaN() || this.isInfinite()) (0.0)
else BigDecimal(this).setScale(n, RoundingMode.HALF_UP).toDouble()

fun Double.roundTo2DecimalPlaces() = roundToDigit(2)

fun Double.roundTo3DecimalPlaces() = roundToDigit(3)

fun Double.toWeight(): String = "%.3f".format(this)
fun Double.removeTrailing(): String = DecimalFormat("0.#").format(this)

fun String.asDouble(): Double = this.toDoubleOrNull() ?: .0

fun roundToNearestNonZeroDecimal(number: Double): String = number.toBigDecimal().stripTrailingZeros().toPlainString()

fun <T : Comparable<T>> T.toEditable(): Editable? = Editable.Factory.getInstance().newEditable(this.toString())

fun Map<*, *>.cleanUpNulls(): Map<*, *> =
    this.mapNotNull {
        if (it.value == null) {
            null
        } else {
            when (it.value) {
                is Map<*, *> -> {
                    it.key to (it.value as Map<*, *>).cleanUpNulls()
                }

                is List<*> -> {
                    it.key to cleanUpNullsInList(it.value as List<*>)
                }

                else -> it.key to it.value
            }
        }
    }.toMap()

private fun Map<*, *>.cleanUpNullsInList(list: List<*>): List<*> {
    return list.mapNotNull { item ->
        when (item) {
            is Map<*, *> -> {
                item.cleanUpNulls()
            }

            is List<*> -> {
                cleanUpNullsInList(item)
            }

            else -> {
                item
            }
        }
    }
}

fun String.removeNewLineAndNonPrintableCharacters() = this.replace(Regex("[\\n\\p{C}]"), "")
fun Double.toNonWeightQuantity() = this.toInt()

fun String.currencyDouble(): Double {
    val number = this.toDoubleOrNull()
    return when {
        number != null && number >= 10 -> number / 100
        number != null && number < 10 -> number / 10
        else -> 0.0 // Default value if conversion fails
    }
}

fun String.replaceLast(oldValue: String, newValue: String): String {
    val lastIndex = lastIndexOf(oldValue)
    return if (lastIndex == -1) {
        // If the substring isn't found, return the original string
        this
    } else {
        val before = substring(0, lastIndex)
        val after = substring(lastIndex + oldValue.length)
        before + newValue + after
    }
}

fun Char.isNewLine(): Boolean {
    return Regex("(\\r\\n)|\\n|\\r").matches(this.toString())
}

