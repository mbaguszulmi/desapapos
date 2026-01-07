package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentEditPrinterBinding
import com.desapabandara.pos.local_db.entity.PrinterEntity
import com.desapabandara.pos.printer.model.PaperWidth
import com.desapabandara.pos.ui.adapter.PrinterLocationSelectionAdapter
import com.desapabandara.pos.ui.viewmodel.ARG_PRINTER_DATA
import com.desapabandara.pos.ui.viewmodel.EditPrinterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPrinterFragment : Fragment() {

    private lateinit var binding: FragmentEditPrinterBinding
    private val editPrinterViewModel by viewModels<EditPrinterViewModel>()

    private val locationSelectionAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PrinterLocationSelectionAdapter { selection ->
            editPrinterViewModel.toggleSelection(selection)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPrinterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeLocations()
    }

    private fun observeLocations() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            editPrinterViewModel.locations.collect {
                locationSelectionAdapter.submitList(it)
            }

        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = editPrinterViewModel

            topBar.apply {
                title = getString(R.string.edit_printer)
                ivMenuBack.setOnClickListener {
                    editPrinterViewModel.dismiss()
                }
            }

            paperWidthGroup.setOnCheckedChangeListener { _, i ->
                when (i) {
                    R.id.paper_width_58 -> {
                        editPrinterViewModel.selectPaperWidth(PaperWidth.W58)
                    }
                    R.id.paper_width_80 -> {
                        editPrinterViewModel.selectPaperWidth(PaperWidth.W80)
                    }
                }
            }

            rvPrinterLocations.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = locationSelectionAdapter
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(printerData: PrinterEntity) =
            EditPrinterFragment().apply {
                arguments = bundleOf(ARG_PRINTER_DATA to printerData)
            }
    }
}