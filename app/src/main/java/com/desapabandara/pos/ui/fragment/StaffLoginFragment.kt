package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentStaffLoginBinding
import com.desapabandara.pos.ui.viewmodel.StaffLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StaffLoginFragment : Fragment() {

    private lateinit var binding: FragmentStaffLoginBinding
    private val staffLoginViewModel by viewModels<StaffLoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStaffLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = staffLoginViewModel

            layoutPinPad.numVM = staffLoginViewModel
        }
    }
}