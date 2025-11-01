package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.databinding.FragmentSetOrderTypeBinding
import com.desapabandara.pos.model.ui.StaffAdapterDisplay
import com.desapabandara.pos.ui.adapter.StaffSelectorArrayAdapter
import com.desapabandara.pos.ui.viewmodel.SetOrderTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetOrderTypeFragment : Fragment() {

    private lateinit var binding: FragmentSetOrderTypeBinding
    private val setOrderTypeViewModel by viewModels<SetOrderTypeViewModel>()
    private var staffAdapter: StaffSelectorArrayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetOrderTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeStaffs()
        observeSelectedWaiter()
    }

    private fun observeSelectedWaiter() {
        observeOnLifecycle(Lifecycle.State.STARTED) {
            setOrderTypeViewModel.selectedWaiter.collect { selectedWaiter ->
                setWaiterText(selectedWaiter)
            }
        }
    }

    private fun setWaiterText(selectedWaiter: Pair<Int, StaffAdapterDisplay>?) {
        selectedWaiter?.let {
            binding.autoCompleteWaiter.apply {
                if (adapter.count > it.first) {
                    setText(it.second.name, false)
                }
            }
        } ?: run {
            binding.autoCompleteWaiter.setText("", false)
        }
    }

    private fun observeStaffs() {
        observeOnLifecycle(Lifecycle.State.STARTED) {
            setOrderTypeViewModel.staffs.collect {
                staffAdapter = StaffSelectorArrayAdapter(requireContext(), it)
                binding.autoCompleteWaiter.setAdapter(staffAdapter)
                setWaiterText(setOrderTypeViewModel.selectedWaiter.value)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = setOrderTypeViewModel

            autoCompleteWaiter.apply {
                onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                    val selected = parent.getItemAtPosition(position) as? StaffAdapterDisplay
                    selected?.let {
                        setOrderTypeViewModel.setSelectedWaiter(it)
                    }
                }

                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        showDropDown()
                    }
                }
            }
        }
    }
}