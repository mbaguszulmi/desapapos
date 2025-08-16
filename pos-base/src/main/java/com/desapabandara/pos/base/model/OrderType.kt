package com.desapabandara.pos.base.model

enum class OrderType(val id: Int) {
    EatIn(1),
    Takeaway(2);

    companion object {
        fun fromId(id: Int) = entries.find {
            it.id == id
        }
    }
}