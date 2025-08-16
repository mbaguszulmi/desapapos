package com.desapabandara.pos.base.model

enum class PaymentStatus(val id: Int) {
    Open(0),
    Settled(1);

    companion object {
        fun fromId(id: Int) = entries.find {
            it.id == id
        }
    }
}
