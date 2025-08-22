package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import co.mbznetwork.android.base.extension.showKeyboard
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentProductSearchBinding
import com.desapabandara.pos.ui.adapter.ProductSearchAdapter
import com.desapabandara.pos.ui.viewmodel.ProductSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductSearchFragment : Fragment() {

    private lateinit var binding: FragmentProductSearchBinding
    private val productSearchViewModel by viewModels<ProductSearchViewModel>()

    private val productSearchAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductSearchAdapter {
            productSearchViewModel.selectProduct(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeProducts()
    }

    private fun observeProducts() {
        observeOnLifecycle(Lifecycle.State.STARTED) {
            productSearchViewModel.products.collect {
                productSearchAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = productSearchViewModel

            etSearch.apply {
                requestFocus()
                requireContext().showKeyboard(this)

                addTextChangedListener {
                    productSearchViewModel.sendSearchKeys(it.toString())
                }
            }

            rvProducts.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = productSearchAdapter
            }
        }
    }

}