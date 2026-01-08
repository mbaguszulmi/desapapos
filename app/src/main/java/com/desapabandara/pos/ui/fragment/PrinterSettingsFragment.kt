package com.desapabandara.pos.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import co.mbznetwork.android.base.extension.observeOnLifecycle
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.FragmentPrinterSettingsBinding
import com.desapabandara.pos.ui.adapter.PrinterListAdapter
import com.desapabandara.pos.ui.viewmodel.PrinterSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrinterSettingsFragment : Fragment() {

    private lateinit var binding: FragmentPrinterSettingsBinding
    private val printerSettingsViewModel by viewModels<PrinterSettingsViewModel>()

    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            launchPrinterScan()
        }
    }

    private val printerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PrinterListAdapter({
            printerSettingsViewModel.connectPrinter(it)
        }, {
            printerSettingsViewModel.printTestPage(it)
        }, {
            printerSettingsViewModel.deletePrinter(it)
        }, {
            printerSettingsViewModel.editPrinter(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrinterSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observePrinters()
    }

    private fun observePrinters() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            printerSettingsViewModel.printers.collect {
                printerAdapter.submitList(it)
            }
        }
    }

    private fun initView() {
        binding.apply {
            rvPrinters.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = printerAdapter
            }
            btnAddPrinter.setOnClickListener {
                launchPrinterScan()
            }

            mainTopBar?.apply {
                title = getString(R.string.printer_settings)
                ivMenuBack.setOnClickListener {
                    printerSettingsViewModel.dismiss()
                }
            }
        }
    }

    private fun launchPrinterScan() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH)
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_SCAN)
        } else {
            printerSettingsViewModel.addNewPrinter()
        }
    }

}