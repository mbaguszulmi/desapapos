package co.mbznetwork.android.base.util

object PhoneNumberUtil {
    private const val defaultCountryCode = "61"
    private const val phoneMaxLength = 12
    private const val phoneMinLength = 8
    private val validPhoneAreaCode = listOf(
        "04",
    )

    fun prefixCountryCode(mobile: String, code: String = defaultCountryCode): String {
        return if (mobile.isNotBlank() && mobile[0] == '0') {
            "+$code${mobile.substring(1)}"
        } else if (Regex("^$code[0-9]+$").matches(mobile)) {
            "+$mobile"
        } else if (Regex("""^\+$code[0-9]+$""").matches(mobile)) {
            mobile
        } else {
            "+$code$mobile"
        }
    }

    fun isPhoneNumberMatch(mobile1: String, mobile2: String): Boolean {
        return getPhoneNumberComponent(mobile1) == getPhoneNumberComponent(mobile2)
    }

    private fun getPhoneNumberComponent(mobile: String): PhoneNumberComponent? {
        var number = mobile.trim().replace(" ", "")
        number = number.replace("-", "")
        val regex = Regex("""\+?(9[976]\d|8[987530]\d|6[987]\d|5[90]\d|42\d|3[875]\d|2[98654321]\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1|0)(\d{1,14})${'$'}""")
        return regex.matchEntire(number)?.let {
            PhoneNumberComponent(if (it.groupValues[1] == "0") defaultCountryCode
            else it.groupValues[1], it.groupValues[2], "0${it.groupValues[2][0]}")
        }
    }

    fun getPhoneEnding(mobile: String): String {
        return mobile.substring(mobile.length-4)
    }

    fun isPhoneNumberValid(mobile: String, countryCode: String? = null): Boolean {
        return (if (countryCode.isNullOrBlank()) mobile
        else prefixCountryCode(mobile, countryCode)).let {phone ->
            getPhoneNumberComponent(phone)?.let {
                it.countryCode == defaultCountryCode && it.mobile.length+1 in phoneMinLength..phoneMaxLength
                        && it.areaCode in validPhoneAreaCode
            } ?: false
        }
    }
}

data class PhoneNumberComponent(
    val countryCode: String,
    val mobile: String,
    val areaCode: String,
)