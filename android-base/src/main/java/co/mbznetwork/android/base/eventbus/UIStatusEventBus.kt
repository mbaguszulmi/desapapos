package co.mbznetwork.android.base.eventbus


import co.mbznetwork.android.base.di.DefaultDispatcher
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIStatusEventBus @Inject constructor(@DefaultDispatcher defaultDispatcher: CoroutineDispatcher) {
    private val uiStatusEventBusScope = CoroutineScope(defaultDispatcher + SupervisorJob())
    private val _uiStatus = MutableSharedFlow<UiStatus>()
    val uiStatus = _uiStatus.asSharedFlow()

    fun setUiStatus(uiStatus: UiStatus) {
        uiStatusEventBusScope.launch {
            _uiStatus.emit(uiStatus)
        }
    }

    fun showErrorMessage(message: String) = setUiStatus(UiStatus.ShowError(UiMessage.StringMessage(message)))

    fun showErrorMessage(message: Int) = setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(message)))

    fun showErrorMessage(message: Int, title: Int) =
        setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(message), UiMessage.ResourceMessage(title)))

    fun showErrorMessage(message: Int, format: List<Any>? = null) = setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(message, format)))

    fun showErrorMessage(message: Int, messageFormat: List<Any>? = null, title: Int, titleFormat: List<Any>? = null) =
        setUiStatus(UiStatus.ShowError(UiMessage.ResourceMessage(message, messageFormat), UiMessage.ResourceMessage(title, titleFormat)))

    fun showIdle() = setUiStatus(UiStatus.Idle)

    fun showLoading() = setUiStatus(UiStatus.Loading)

    fun showMessage(message: String) = setUiStatus(UiStatus.ShowMessage(UiMessage.StringMessage(message)))

    fun showMessage(message: Int) = setUiStatus(UiStatus.ShowMessage(UiMessage.ResourceMessage(message)))
}