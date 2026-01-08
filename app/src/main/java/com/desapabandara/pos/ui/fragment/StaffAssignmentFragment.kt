package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentStaffAssignmentBinding
import com.desapabandara.pos.ui.adapter.StaffAssignmentAdapter
import com.desapabandara.pos.ui.viewmodel.StaffLocationAssignmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffAssignmentFragment : Fragment() {

    private lateinit var binding: FragmentStaffAssignmentBinding
    private val staffAssignmentViewModel by viewModels<StaffLocationAssignmentViewModel>()

    private val staffAssignmentAdapter by lazy(LazyThreadSafetyMode.NONE) {
        StaffAssignmentAdapter { staffId, btn ->
            showAssignmentOptions(staffId, btn)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeStaffAssignments()
    }

    private fun observeStaffAssignments() {
        observeOnLifecycle(Lifecycle.State.STARTED) {
            staffAssignmentViewModel.staffAssignments.collect { assignments ->
                staffAssignmentAdapter.submitList(assignments)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            mainTopBar?.apply {
                title = getString(R.string.staff_assignments)
                ivMenuBack.setOnClickListener {
                    staffAssignmentViewModel.dismiss()
                }
            }

            rvStaffs.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = staffAssignmentAdapter
            }
        }
    }

    private fun showAssignmentOptions(staffId: String, view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menu.apply {
            staffAssignmentViewModel.locations.value.forEach { location ->
                add(Menu.NONE, location.intId, Menu.NONE, location.name)
            }
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            staffAssignmentViewModel.assignLocationToStaff(staffId, menuItem.itemId.toString())
            true
        }
        popupMenu.show()
    }
}
