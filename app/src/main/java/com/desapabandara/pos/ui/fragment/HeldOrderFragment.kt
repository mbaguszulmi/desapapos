package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentHeldOrderBinding
import com.desapabandara.pos.model.ui.MainMenu
import com.desapabandara.pos.ui.adapter.HeldOrderAdapter
import com.desapabandara.pos.ui.viewmodel.HeldOrderViewModel
import com.desapabandara.pos.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeldOrderFragment : Fragment() {

    private lateinit var binding: FragmentHeldOrderBinding
    private val heldOrderViewModel by viewModels<HeldOrderViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val orderAdapter by lazy(LazyThreadSafetyMode.NONE) {
        HeldOrderAdapter({
            heldOrderViewModel.loadOrder(it)
        }, {
            heldOrderViewModel.showOrderDetail(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeldOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeHeldOrders()
        observeShowPos()
    }

    private fun observeShowPos() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            heldOrderViewModel.showPos.collect {
                if (it) mainViewModel.selectMainMenu(MainMenu.POS)
            }
        }
    }

    private fun observeHeldOrders() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            heldOrderViewModel.heldOrders.collect {
                orderAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            mainTopBar.menuVM = mainViewModel

            heldOrderRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = orderAdapter
            }
        }
    }

}