package com.desapabandara.pos.base.model

enum class OrderStatus(val id: Int) {
    Active(1),
    Sent(2),
    Preparing(3),
    Prepared(4),
    Completed(5),
    Refunded(6);

    companion object {
        fun fromId(id: Int) = entries.find {
            it.id == id
        }
    }
}
