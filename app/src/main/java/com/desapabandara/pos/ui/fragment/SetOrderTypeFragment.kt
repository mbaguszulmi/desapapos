package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentSetOrderTypeBinding
import com.desapabandara.pos.ui.viewmodel.SetOrderTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetOrderTypeFragment : Fragment() {

    private lateinit var binding: FragmentSetOrderTypeBinding
    private val setOrderTypeViewModel by viewModels<SetOrderTypeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetOrderTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = setOrderTypeViewModel
        }
    }
}