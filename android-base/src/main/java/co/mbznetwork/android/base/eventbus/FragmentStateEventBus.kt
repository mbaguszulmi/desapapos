package co.mbznetwork.android.base.eventbus

import androidx.fragment.app.Fragment
import co.mbznetwork.android.base.di.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FragmentStateEventBus @Inject constructor(dispatcherProvider: DispatcherProvider) {

    private val posStateScope = CoroutineScope(dispatcherProvider.io + SupervisorJob())

    private val _currentState = MutableSharedFlow<FragmentState>()
    val currentState = _currentState.asSharedFlow()

    fun currentStateFinished(obj: Any? = null, clearAll: Boolean = false) {
        posStateScope.launch {
            _currentState.emit(FragmentState.NA(obj, clearAll))
        }
    }

    fun setCurrentState(fragment: Fragment, keep: Boolean = false) {
        posStateScope.launch {
            _currentState.emit(FragmentState.ShowScreen(fragment, keep))
        }
    }

    suspend inline fun <reified T>awaitStateResult(): T {
        return currentState.filterIsInstance<FragmentState.NA>().mapNotNull {
            it.obj?.let { obj ->
                if (obj is T) obj
                else null
            }
        }.first()
    }

}

sealed class FragmentState {
    data class NA(val obj: Any? = null, val clearAll: Boolean = false) : FragmentState()
    data class ShowScreen(
        val fragment: Fragment,
        val keep: Boolean = false,
        val modal: Boolean = false
    ) : FragmentState()
}

