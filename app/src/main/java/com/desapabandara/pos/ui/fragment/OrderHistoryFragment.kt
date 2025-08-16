package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.databinding.FragmentOrderHistoryBinding
import com.desapabandara.pos.ui.adapter.OrderHistoryAdapter
import com.desapabandara.pos.ui.viewmodel.MainViewModel
import com.desapabandara.pos.ui.viewmodel.OrderHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    private val orderHistoryViewModel by viewModels<OrderHistoryViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val orderHistoryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        OrderHistoryAdapter {
            orderHistoryViewModel.showDetails(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeOrders()
    }

    private fun observeOrders() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            orderHistoryViewModel.orders.collect {
                orderHistoryAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            mainTopBar.menuVM = mainViewModel

            orderRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = orderHistoryAdapter
            }
        }
    }
}