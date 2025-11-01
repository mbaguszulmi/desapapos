package com.desapabandara.pos.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.desapabandara.pos.databinding.ItemStaffAssignmentBinding
import com.desapabandara.pos.model.ui.StaffAssignmentDisplay

class StaffAssignmentAdapter(
    private val onAssignClick: (String, View) -> Unit
): ListAdapter<StaffAssignmentDisplay, StaffAssignmentAdapter.StaffAssignmentViewHolder>(STAFF_ASSIGNMENT_DIFF) {
    inner class StaffAssignmentViewHolder(
        private val binding: ItemStaffAssignmentBinding
    ): ViewHolder(binding.root) {
        fun bind(item: StaffAssignmentDisplay) {
            binding.apply {
                staffAssignment = item
                tvAssign.setOnClickListener {
                    onAssignClick(item.staffId, tvAssign)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffAssignmentViewHolder {
        return StaffAssignmentViewHolder(
            ItemStaffAssignmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StaffAssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private val STAFF_ASSIGNMENT_DIFF = object: DiffUtil.ItemCallback<StaffAssignmentDisplay>() {
    override fun areItemsTheSame(oldItem: StaffAssignmentDisplay, newItem: StaffAssignmentDisplay): Boolean {
        return oldItem.staffId == newItem.staffId
    }

    override fun areContentsTheSame(oldItem: StaffAssignmentDisplay, newItem: StaffAssignmentDisplay): Boolean {
        return oldItem == newItem
    }

}
