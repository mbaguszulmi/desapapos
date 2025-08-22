package co.mbznetwork.android.base.extension

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.mbznetwork.android.base.util.GridSpacingItemDecoration
import kotlin.math.floor
import kotlin.math.max

interface FragmentKtx {
    fun requireActivity(): Activity

    val Float.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, requireActivity().resources.displayMetrics
        )

    val Int.dp get() = this.toFloat().dp
}

fun RecyclerView.setupDynamicGrid(cellWidth: Float, spacing: Int, edgeSpacing: Boolean = false) {
    layoutManager = GridLayoutManager(context, 1)
    addItemDecoration(GridSpacingItemDecoration(1, spacing, edgeSpacing))

    viewTreeObserver.addOnGlobalLayoutListener {
        val spanCount = max(1, floor(width / cellWidth).toInt())
        (getItemDecorationAt(0) as GridSpacingItemDecoration).spanCount = spanCount
        (layoutManager as GridLayoutManager).spanCount = spanCount
        invalidateItemDecorations()
    }
}

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}
