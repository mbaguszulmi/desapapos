package com.desapabandara.pos.ui.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import co.mbznetwork.android.base.eventbus.FragmentState
import co.mbznetwork.android.base.eventbus.FragmentStateEventBus
import co.mbznetwork.android.base.eventbus.UIStatusEventBus
import co.mbznetwork.android.base.extension.observeOnLifecycle
import co.mbznetwork.android.base.model.PopUpMessage
import co.mbznetwork.android.base.model.UiMessage
import co.mbznetwork.android.base.model.UiStatus
import com.desapabandara.pos.R
import com.desapabandara.pos.databinding.ActivityMainBinding
import com.desapabandara.pos.databinding.PopupErrorBinding
import com.desapabandara.pos.databinding.PopupMessageBinding
import com.desapabandara.pos.model.ui.MainMenu
import com.desapabandara.pos.model.ui.MainScreen
import com.desapabandara.pos.ui.fragment.HeldOrderFragment
import com.desapabandara.pos.ui.fragment.LoginFragment
import com.desapabandara.pos.ui.fragment.OrderHistoryFragment
import com.desapabandara.pos.ui.fragment.PosFragment
import com.desapabandara.pos.ui.fragment.SettingsFragment
import com.desapabandara.pos.ui.fragment.SyncFragment
import com.desapabandara.pos.ui.popup.MessagePopup
import com.desapabandara.pos.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TOP_OPERATION_KEEP_TAG = "TOP_OPERATION_KEEP_TAG"
const val TOP_OPERATION_TAG = "TOP_OPERATION_TAG"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var uiStateEventBus: UIStatusEventBus

    @Inject
    lateinit var fragmentStateEventBus: FragmentStateEventBus

    private val mainViewModel by viewModels<MainViewModel>()

    private var popupDialog: PopupWindow? = null
    private val loadingDialog: PopupWindow by lazy {
        PopupWindow(
            LayoutInflater.from(this).inflate(
                R.layout.dialog_loading, binding.root as ViewGroup, false
            ), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ).apply { isOutsideTouchable = false }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initViewBinding()
        observeUiStatus()
        observeMainScreen()
        observeActiveMainMenu()
        observeOpenDrawer()
        observeFragmentState()
    }

    private fun observeFragmentState() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            fragmentStateEventBus.currentState.collect {
                when (it) {
                    is FragmentState.NA -> {
                        if (it.clearAll) {
                            supportFragmentManager.fragments.forEach { f ->
                                if (f != null) {
                                    supportFragmentManager.beginTransaction().remove(f).commit()
                                }
                            }
                        } else {
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            supportFragmentManager.findFragmentByTag(TOP_OPERATION_KEEP_TAG)
                                ?.let { f ->
                                    fragmentTransaction.remove(f).commit()
                                } ?: supportFragmentManager.findFragmentByTag(TOP_OPERATION_TAG)
                                ?.let { f ->
                                    fragmentTransaction.remove(f).commit()
                                }
                        }
                    }

                    is FragmentState.ShowScreen -> {
                        val fragmentTransaction = supportFragmentManager.beginTransaction()
                        if (it.keep) {
                            if (it.fragment.isAdded) {
                                return@collect
                            }
                            fragmentTransaction.add(
                                R.id.top_operation_container,
                                it.fragment,
                                TOP_OPERATION_KEEP_TAG
                            ).commit()
                        } else {
                            fragmentTransaction.replace(
                                R.id.top_operation_container, it.fragment,
                                TOP_OPERATION_TAG
                            ).commit()
                        }
                    }
                }
            }
        }
    }

    private fun observeOpenDrawer() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.showMenuDrawer.collect {
                binding.mainDrawerLayout.run {
                    if (it) {
                        openDrawer(GravityCompat.START)
                    } else {
                        closeDrawers()
                    }
                }
            }
        }
    }

    private fun observeActiveMainMenu() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.activeMainMenu.collect {
                when(it) {
                    MainMenu.POS -> PosFragment()
                    MainMenu.HeldOrder -> HeldOrderFragment()
//                    MainMenu.OrderHistory -> OrderHistoryFragment()
                    MainMenu.Settings -> SettingsFragment()
                    else -> null
                }?.let { fragment ->
                    navigateTo(fragment)
                }
            }
        }
    }

    private fun observeMainScreen() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            mainViewModel.mainScreen.collect {
                when(it) {
                    MainScreen.Login -> LoginFragment()
                    MainScreen.Sync -> SyncFragment()
                    MainScreen.Main -> {
                        mainViewModel.selectMainMenu(MainMenu.POS)
                        null
                    }
                    else -> null
                }?.let {  fragment ->
                    navigateTo(fragment)
                }
            }
        }
    }

    private fun navigateTo(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            fragment
        ).commit()
    }

    private fun initViewBinding() {
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        binding.apply {
            lifecycleOwner = this@MainActivity
            drawerView.mainVM = mainViewModel
            mainDrawerLayout.apply {
                setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                addDrawerListener(object : DrawerLayout.DrawerListener {
                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                    }

                    override fun onDrawerOpened(drawerView: View) {

                    }

                    override fun onDrawerClosed(drawerView: View) {
                        mainViewModel.closeMenuDrawer()
                    }

                    override fun onDrawerStateChanged(newState: Int) {

                    }

                })
            }
        }
    }

    private fun dismissLoadingDialog() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    private fun showLoadingDialog() {
        loadingDialog.showAtLocation(binding.root, Gravity.TOP, 0, 0)
    }

    private fun getMessageInString(message: UiMessage) = when (message) {
        is UiMessage.ResourceMessage -> {
            try {
                if (message.formatArgs.isNotEmpty()) String.format(
                    getString(
                        message.id, *message.formatArgs
                    )
                )
                else getString(message.id)
            } catch (e: Exception) {
                ""
            }
        }

        is UiMessage.StringMessage -> message.message
    }

    private fun getPopUpMessage(
        message: UiMessage, title: UiMessage?, logo: Int?
    ): PopUpMessage {
        val messageContent = getMessageInString(message)
        val messageTitle = title?.let {
            getMessageInString(it)
        } ?: getString(R.string.error_occurred)
        return PopUpMessage(messageTitle, messageContent, logo)
    }

    private fun showError(message: UiMessage, title: UiMessage?, logo: Int?) {
        DataBindingUtil.inflate<PopupErrorBinding>(
            LayoutInflater.from(this), R.layout.popup_error, null, false
        ).also {
            it.message = getPopUpMessage(message, title, logo)

            it.closeErrorDialog.setOnClickListener {
                popupDialog?.dismiss()
            }
            popupDialog = MessagePopup(
                it.root,
                resources.getDimensionPixelSize(R.dimen.pop_up_message_height),
                lifecycleScope
            ).apply {
                isOutsideTouchable = true
                showAtLocation(binding.root, Gravity.TOP, 0, 0)
            }
        }
    }

    private fun showMessageDialog(message: UiMessage, title: UiMessage?, logo: Int?) {
        DataBindingUtil.inflate<PopupMessageBinding>(
            LayoutInflater.from(this), R.layout.popup_message, null, false
        ).also {
            logo?.let { src -> it.popMessageLogo.setImageResource(src) }

            it.message = getPopUpMessage(
                message,
                title ?: UiMessage.ResourceMessage(R.string.success),
                logo
            )

            it.btnCloseDialog.setOnClickListener {
                popupDialog?.dismiss()
            }

            popupDialog = MessagePopup(
                it.root,
                resources.getDimensionPixelSize(R.dimen.pop_up_message_height),
                lifecycleScope
            ).apply {
                isOutsideTouchable = true
                showAtLocation(binding.root, Gravity.TOP, 0, 0)
            }
        }
    }

    private fun observeUiStatus() {
        observeOnLifecycle(Lifecycle.State.CREATED) {
            uiStateEventBus.uiStatus.collect {
                when (it) {
                    UiStatus.Idle -> {
                        dismissLoadingDialog()
                    }
                    UiStatus.Loading -> {
                        showLoadingDialog()
                    }
                    is UiStatus.ShowError -> {
                        dismissLoadingDialog()
                        showError(it.errorMsg, it.title, it.logo)
                    }
                    is UiStatus.ShowMessage -> {
                        dismissLoadingDialog()
                        showMessageDialog(it.message, it.title, it.logo)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}