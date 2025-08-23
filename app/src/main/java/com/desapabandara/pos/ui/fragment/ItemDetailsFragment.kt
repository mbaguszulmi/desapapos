package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.desapabandara.pos.databinding.FragmentItemDetailsBinding
import com.desapabandara.pos.local_db.entity.OrderItemEntity
import com.desapabandara.pos.ui.viewmodel.ItemDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

const val ARG_ITEM = "ARG_ITEM"

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
        fun newInstance(item: OrderItemEntity) =
            ItemDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ITEM, item)
                }
            }
    }
}