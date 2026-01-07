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
import com.desapabandara.pos.databinding.FragmentPaymentBinding
import com.desapabandara.pos.ui.adapter.PaymentMethodAdapter
import com.desapabandara.pos.ui.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : Fragment(), FragmentKtx {

    private lateinit var binding: FragmentPaymentBinding
    private val paymentViewModel by viewModels<PaymentViewModel>()

    private val paymentMethodAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PaymentMethodAdapter {
            paymentViewModel.selectPayment(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observePaymentMethods()
    }

    private fun observePaymentMethods() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            paymentViewModel.paymentMethods.collect {
                paymentMethodAdapter.submitList(it)
            }

        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            numVM = paymentViewModel
            topBar.apply {
                title = getString(R.string.payment)
                ivMenuBack.setOnClickListener { paymentViewModel.cancel() }
            }
            rvPaymentMethods.apply {
                adapter = paymentMethodAdapter
                setupDynamicGrid(150.dp, 16.dp.toInt())
                itemAnimator = null
            }
        }
    }

}