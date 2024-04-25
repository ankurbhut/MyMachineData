package com.practical.mydatamachine.shared.base.activitybase

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import com.practical.mydatamachine.databinding.LayoutToolbarBinding
import com.practical.mydatamachine.localcache.LocalDataHelper
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.ViewIntent
import com.practical.mydatamachine.shared.base.ViewState
import com.practical.mydatamachine.shared.extension.hapticFeedbackEnabled

abstract class ToolBarBaseActivity<VDB : ViewDataBinding, INTENT : ViewIntent, STATE : ViewState,
        VM : AdvanceBaseViewModel<INTENT, STATE>>(
    bindingFactory: (LayoutInflater) -> VDB,
    modelClass: Class<VM>,
) : NavigationBaseActivity<VDB, INTENT, STATE, VM>(bindingFactory, modelClass) {
    private var mToolbarBinding: LayoutToolbarBinding? = null

    var currentNavController: NavController? = null

    abstract fun getToolBarBinding(): LayoutToolbarBinding?

    open fun hideBottomNavigation() {}

    open fun onTabChangeRequest(itemId: Int) {}

    override fun setContentView(view: View?) {
        super.setContentView(view)
        initToolBarView(view)
    }

    private fun initToolBarView(view: View?) {
        view?.let {
            mToolbarBinding = getToolBarBinding()
            setToolBarListeners(mToolbarBinding)
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        mToolbarBinding?.appTitle = title.toString()
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        mToolbarBinding?.appTitle = getString(titleId)
    }

    fun setToolBarListeners(mToolbarBinding: LayoutToolbarBinding?) {
        val toolbarListeners = View.OnClickListener {
            it?.hapticFeedbackEnabled()
            when (it) {
              /*  mToolbarBinding?.btnBack -> {
                   onSuperBackPressed()
                }*/

                mToolbarBinding?.ivSettings -> {
                  //  openSettingsActivity()
                }

                mToolbarBinding?.userImage -> {
                    //changeBottomNavItem(R.id.nav_profile)
                }

                mToolbarBinding?.btnNotification -> {
                    //openNotificationActivity()
                }
            }
        }
        mToolbarBinding?.clickListener = toolbarListeners
    }

    open fun onSwitchClicked(checked: Boolean?) {
        checked?.let { LocalDataHelper.isNotificationOn = it }
    }

    override fun onUserInfoUpdate() {
        super.onUserInfoUpdate()
    }
}