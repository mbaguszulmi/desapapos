package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentSaleCompletedBinding
import com.desapabandara.pos.ui.viewmodel.SaleCompletedViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_AMOUNT_TENDERED = "ARG_AMOUNT_TENDERED"
const val ARG_CHANGE_REQUIRED = "ARG_CHANGE_REQUIRED"

@AndroidEntryPoint
class SaleCompletedFragment : Fragment() {

    private lateinit var binding: FragmentSaleCompletedBinding
    private val saleCompletedViewModel by viewModels<SaleCompletedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSaleCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = saleCompletedViewModel
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(amountTendered: Double, changeRequired: Double) =
            SaleCompletedFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_AMOUNT_TENDERED, amountTendered)
                    putDouble(ARG_CHANGE_REQUIRED, changeRequired)
                }
            }
    }
}