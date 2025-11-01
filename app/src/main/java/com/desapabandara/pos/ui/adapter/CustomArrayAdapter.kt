package com.desapabandara.pos.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter

abstract class CustomArrayAdapter<T>(
    context: Context,
    private val list: MutableList<T>
): ArrayAdapter<T>(context, android.R.layout.simple_dropdown_item_1line, list) {

    private val originalList = list.toList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val item = getItem(position)
        if (item != null) {
            (view as? android.widget.TextView)?.text = getText(item)
        }
        return view
    }

    abstract fun getText(item: T): String

    override fun getFilter(): Filter {
        // filter only from the getText
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    results.values = originalList.toList()
                    results.count = originalList.size
                } else {
                    val filteredList = originalList.filter { getText(it).contains(constraint, ignoreCase = true) }
                    results.values = filteredList
                    results.count = filteredList.size
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list.clear()
                @Suppress("UNCHECKED_CAST")
                list.addAll(results?.values as? List<T> ?: emptyList())
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                @Suppress("UNCHECKED_CAST")
                return (resultValue as? T)?.let {
                    getText(it)
                } ?: super.convertResultToString(resultValue)
            }
        }
    }
}