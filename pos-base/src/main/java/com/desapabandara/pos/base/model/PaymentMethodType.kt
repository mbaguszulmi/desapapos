package com.desapabandara.pos.base.model

enum class PaymentMethodType(val id: Int, val typeName: String) {
    Cash(1, "Cash"),
    Qris(2, "QRIS"),
    Debit(3, "Debit"),
    Credit(4, "Credit"),
    Other(21, "Other");

    companion object {
        fun fromId(id: Int): PaymentMethodType {
            return entries.firstOrNull { it.id == id } ?: Other
        }
    }
}