package com.desapabandara.pos.model.ui

import com.desapabandara.pos.base.model.ItemInfo

sealed class ItemDetailsResult {
    data object Cancelled: ItemDetailsResult()
    data class Saved(
        val info: ItemInfo
    ): ItemDetailsResult()
}