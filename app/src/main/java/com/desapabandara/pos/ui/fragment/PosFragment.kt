package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentPosBinding
import com.desapabandara.pos.ui.adapter.CategoryPosAdapter
import com.desapabandara.pos.ui.viewmodel.MainViewModel
import com.desapabandara.pos.ui.viewmodel.PosViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PosFragment : Fragment() {

    private lateinit var binding: FragmentPosBinding

    private val posViewModel by viewModels<PosViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val categoryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        CategoryPosAdapter {
            posViewModel.selectCategory(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCategories()
        observeSelectedCategory()
    }

    private fun observeSelectedCategory() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            posViewModel.selectedCategory.collect {
                childFragmentManager.beginTransaction().apply {
                    if (it.isNotBlank()) {
                        replace(R.id.product_container, PosProductFragment.newInstance(
                            it
                        )).commit()
                    }
                }
            }
        }
    }

    private fun observeCategories() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            posViewModel.categories.collect {
                categoryAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            posVM = posViewModel

            mainTopBar.menuVM = mainViewModel

            orderSummaryButton.posVM = posViewModel

            categoryRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = categoryAdapter
                itemAnimator = null
            }
        }
    }
}