package com.desapabandara.pos.base.model

enum class ItemStatus(val id: Int) {
    New(1),
    Sent(2),
    Preparing(3),
    Prepared(4),
    Cancelled(5),
    Refunded(6);

    companion object {
        fun fromId(id: Int) = entries.find {
            it.id == id
        }
    }
}
