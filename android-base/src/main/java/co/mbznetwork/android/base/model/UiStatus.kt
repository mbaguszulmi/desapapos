package co.mbznetwork.android.base.model

import androidx.annotation.DrawableRes

sealed class UiStatus {
    object Loading: UiStatus()
    data class ShowError(val errorMsg: UiMessage, val title: UiMessage?=null, @DrawableRes val logo: Int? =null): UiStatus()
    object Idle: UiStatus()
    data class ShowMessage(val message: UiMessage, val title: UiMessage?=null, @DrawableRes val logo: Int? =null): UiStatus()
}

sealed class UiMessage{
    data class ResourceMessage(val id: Int, private val _formatArgs: List<Any>? = null): UiMessage() {
        val formatArgs = _formatArgs?.toTypedArray() ?: emptyArray()
    }

    data class StringMessage(val message: String): UiMessage()
}
