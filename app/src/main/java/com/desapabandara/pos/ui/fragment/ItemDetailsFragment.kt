package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentItemDetailsBinding
import com.desapabandara.pos.ui.viewmodel.ItemDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_ITEM_ID = "ARG_ITEM_ID"

@AndroidEntryPoint
class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val itemDetailsViewModel by viewModels<ItemDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = itemDetailsViewModel
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(itemId: String) =
            ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId)
                }
            }
    }
}