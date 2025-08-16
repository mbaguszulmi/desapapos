package com.desapabandara.pos.base.util

import android.os.Build
import co.mbznetwork.android.base.extension.roundToDigit
import co.mbznetwork.android.base.extension.toCentString
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.round

@Singleton
class CurrencyUtil @Inject constructor() {
    private val country = "ID"
    private val language = "id"

    fun getCurrentFormat(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale(language, country)).format(amount).let {
            when (country) {
                "ID" -> if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    it.dropLast(3)
                } else it
                else -> it
            }
        }
    }

    fun getAmountValueFromCurrency(amount: String): Double = try {
        NumberFormat.getCurrencyInstance((Locale(language, country))).parse(amount)?.toDouble() ?: 0.0
    } catch (e: Exception) {
        0.0
    }

    private fun roundToNearestIncrementValue(number: Double, roundBy: Double): Double {
        if (number == .0 || roundBy == .0) return number
        if (number.mod(roundBy) == .0) return number
        val absNumber = abs(number)
        val roundedAmount = round(absNumber / roundBy) * roundBy
        return if (number < 0) -roundedAmount else roundedAmount
    }

    fun getCashRoundedAmount(amount: Double): Pair<Double, Double> {
        when (country) {
            "ID" -> {
                if (amount.isNaN()) return Pair(amount, .0)

                val roundBy = .0
                if (roundBy == .0) return Pair(amount, .0)

                val roundedAmount = roundToNearestIncrementValue(amount, roundBy)
                val roundingAmount = roundedAmount - amount
                return Pair(roundedAmount, roundingAmount)
            }

            "AU", "SG" -> {
                val amountInString = amount.absoluteValue.toCentString(2)
                val rounding = 10 - amountInString.substring(amountInString.length - 1).toInt()
                var roundedAmount = amount.absoluteValue
                val roundingAmount = if (rounding < 3) {
                    (rounding / 100.0)
                } else if (rounding > 7) {
                    ((rounding - 10) / 100.0)
                } else {
                    (rounding / 100.0 - 0.05)
                }
                roundedAmount += roundingAmount
                return Pair(roundedAmount.roundToDigit(2), roundingAmount.roundToDigit(2))
            }

            else -> {
                return Pair(amount.absoluteValue, .0)
            }
        }
    }

    fun getCurrencySymbol(): String {
        return NumberFormat.getCurrencyInstance((Locale(language, country))).currency?.symbol ?: "$"
    }
}