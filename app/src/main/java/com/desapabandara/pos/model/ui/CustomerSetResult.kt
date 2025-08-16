package com.desapabandara.pos.model.ui

sealed class CustomerSetResult {
    data object Cancelled: CustomerSetResult()

    data class Added(
        val id: String,
        val name: String,
        val phoneNumber: String,
        val adultMaleCount: Int,
        val adultFemaleCount: Int,
        val childMaleCount: Int,
        val childFemaleCount: Int,
    ): CustomerSetResult()
}
