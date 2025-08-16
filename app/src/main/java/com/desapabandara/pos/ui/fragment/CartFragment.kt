package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentCartBinding
import com.desapabandara.pos.ui.adapter.OrderItemAdapter
import com.desapabandara.pos.ui.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartViewModel by viewModels<CartViewModel>()

    private val itemAdapter by lazy(LazyThreadSafetyMode.NONE) {
        OrderItemAdapter({
            cartViewModel.removeItem(it)
        }, {
            cartViewModel.showItemDetails(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeOrderItems()
    }

    private fun observeOrderItems() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            cartViewModel.orderItems.collect {
                itemAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = cartViewModel
            topBar.apply {
                title = getString(R.string.cart)
                ivMenuBack.setOnClickListener {
                    cartViewModel.dismiss()
                }
            }

            orderItemRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = itemAdapter
            }
        }
    }
}