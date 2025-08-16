package co.mbznetwork.android.base.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Fragment.observeOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.RESUMED,
    method: suspend CoroutineScope.() -> Unit
) = viewLifecycleOwner.lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(state) {
        method()
    }
}

fun AppCompatActivity.observeOnLifecycle(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    method: suspend CoroutineScope.() -> Unit
) = lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(state) {
        method()
    }
}