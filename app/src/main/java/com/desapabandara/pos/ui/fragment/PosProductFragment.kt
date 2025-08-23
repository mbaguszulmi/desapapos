package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import co.mbznetwork.android.base.extension.FragmentKtx
import co.mbznetwork.android.base.extension.observeOnLifecycle
import co.mbznetwork.android.base.extension.setupDynamicGrid
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentPosProductBinding
import com.desapabandara.pos.ui.adapter.ProductPosAdapter
import com.desapabandara.pos.ui.viewmodel.PosProductViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_CATEGORY_ID = "ARG_CATEGORY_ID"

@AndroidEntryPoint
class PosProductFragment : Fragment(), FragmentKtx {

    private lateinit var binding: FragmentPosProductBinding
    private val posProductViewModel by viewModels<PosProductViewModel>()

    private val productAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductPosAdapter {
            posProductViewModel.addProductToCart(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPosProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeProducts()
    }

    private fun observeProducts() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            posProductViewModel.products.collect {
                productAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            productRv.apply {
                adapter = productAdapter
                setupDynamicGrid(100.dp, 0)
                itemAnimator = null
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(categoryId: String) =
            PosProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_ID, categoryId)
                }
            }
    }
}