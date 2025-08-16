package com.desapabandara.pos.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentScanPrinterBinding
import com.desapabandara.pos.ui.adapter.PrinterScanAdapter
import com.desapabandara.pos.ui.viewmodel.ScanPrinterViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class ScanPrinterFragment : Fragment() {

    private lateinit var binding: FragmentScanPrinterBinding
    private val scanPrinterViewModel by viewModels<ScanPrinterViewModel>()

    private val printerScanAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PrinterScanAdapter {
            scanPrinterViewModel.selectDevice(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanPrinterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observePrinterDevices()
    }

    private fun observePrinterDevices() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            scanPrinterViewModel.printerDevices.collect {
                printerScanAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = scanPrinterViewModel
            rvPrinterDevices.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = printerScanAdapter
            }
        }
    }
}