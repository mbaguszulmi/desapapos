package com.desapabandara.pos.ui.popup

import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessagePopup(root: View, height: Int, private val externalScope: CoroutineScope): PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, height) {

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        externalScope.launch(Dispatchers.Main) {
            delay(3000)
            if (isShowing) {
                dismiss()
            }
        }
    }
}