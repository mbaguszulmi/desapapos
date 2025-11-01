package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentOrderDetailBinding
import com.desapabandara.pos.ui.adapter.OrderItemHistoryAdapter
import com.desapabandara.pos.ui.viewmodel.OrderDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_ORDER_ID = "ARG_ORDER_ID"

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val orderDetailViewModel by viewModels<OrderDetailViewModel>()

    private val orderItemAdapter by lazy(LazyThreadSafetyMode.NONE) {
        OrderItemHistoryAdapter(
            onTogglePrepared = { item ->
                orderDetailViewModel.toggleItemPrepared(item)
            },
            onToggleServed = { item ->
                orderDetailViewModel.toggleItemServed(item)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeOrderItems()
    }

    private fun observeOrderItems() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            orderDetailViewModel.items.collect {
                orderItemAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = orderDetailViewModel

            topBar.apply {
                title = getString(R.string.order_details)
                ivMenuBack.setOnClickListener { orderDetailViewModel.dismiss() }
            }

            itemRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = orderItemAdapter
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) = OrderDetailFragment().apply {
            arguments = bundleOf(ARG_ORDER_ID to id)
        }
    }
}