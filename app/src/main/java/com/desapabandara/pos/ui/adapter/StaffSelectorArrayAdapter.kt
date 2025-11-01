package com.desapabandara.pos.ui.adapter

import android.content.Context
import com.desapabandara.pos.model.ui.StaffAdapterDisplay

class StaffSelectorArrayAdapter(
    context: Context,
    list: MutableList<StaffAdapterDisplay>
): CustomArrayAdapter<StaffAdapterDisplay>(context, list) {
    override fun getText(item: StaffAdapterDisplay): String {
        return item.name
    }
}