package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentAddCustomerBinding
import com.desapabandara.pos.ui.viewmodel.AddCustomerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerFragment : Fragment() {

    private lateinit var binding: FragmentAddCustomerBinding
    private val addCustomerViewModel by viewModels<AddCustomerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = addCustomerViewModel
        }
    }
}