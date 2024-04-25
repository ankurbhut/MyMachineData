package com.practical.mydatamachine.shared.base.viewbase

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practical.mydatamachine.R
import com.practical.mydatamachine.databinding.LayoutErrorViewBinding
import com.practical.mydatamachine.databinding.LayoutToolbarBinding
import com.practical.mydatamachine.shared.base.activitybase.NavigationBaseActivity
import com.practical.mydatamachine.shared.base.activitybase.ToolBarBaseActivity
import com.practical.mydatamachine.shared.extension.hide
import com.practical.mydatamachine.shared.extension.show

abstract class BaseFragment<VDB : ViewDataBinding>(val bindingFactory: (LayoutInflater) -> VDB) :
    Fragment() {

    var mBase: AdvancedBaseActivity<*, *, *, *>? = null
    var mNavigationBase: NavigationBaseActivity<*, *, *, *>? = null
    var mTooBarBase: ToolBarBaseActivity<*, *, *, *>? = null

    open fun onRewardAdsSkipped() {}


    private var _binding: VDB? = null
    protected val mBinding get() = _binding
    private var mContext: Context? = null
    private var mErrorViewBinding: LayoutErrorViewBinding? = null

    private var mOrientation: Int = Configuration.ORIENTATION_PORTRAIT

    abstract fun getToolBarBinding(): LayoutToolbarBinding?
    private fun initToolBarView() {
        mTooBarBase?.setToolBarListeners(getToolBarBinding())
    }

    open fun onSuperBackPressed() {}
    open fun parseArguments() {}
    abstract fun initUI()
    abstract fun initDATA()
    open fun setVariables() {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (context is AdvancedBaseActivity<*, *, *, *>) {
            mBase = context
        }
        if (context is NavigationBaseActivity<*, *, *, *>) {
            mNavigationBase = context
        }
        if (context is ToolBarBaseActivity<*, *, *, *>) {
            mTooBarBase = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        parseArguments()
        mOrientation = resources.configuration.orientation

        initObjects()
        //initErrorView()
        initToolBarView()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initDATA()
        setVariables()
    }

    private fun initObjects() {
        _binding = bindingFactory(layoutInflater)
    }

    private fun initErrorView() {
        mBinding?.root?.let {
            if (it.findViewById<LinearLayout>(R.id.layoutErrorView) != null) {
                mErrorViewBinding = LayoutErrorViewBinding.bind(it)
            }
        }
    }

    fun isLandscapeMode(): Boolean = mOrientation == Configuration.ORIENTATION_LANDSCAPE

    fun isPortraitMode(): Boolean = mOrientation == Configuration.ORIENTATION_PORTRAIT

    @Suppress("unused")
    fun showSnackBar(message: String?) {
        if (mBase == null) return
        mBase?.showSnackBar(mBinding?.root, message)
    }

    @Suppress("unused")
    fun showSnackBar(view: View?, message: String?) {
        if (mBase == null) return
        mBase?.showSnackBar(view, message)
    }

    fun showProgressBar(message: String? = "") {
        if (mBase == null) return
        mBase?.showProgressBar(message = message)
    }

    private fun isProgressViewVisible(): Boolean {
        return mBase?.isProgressVisible() == true
    }

    fun hideProgressBar() {
        if (mBase == null) return
        mBase?.hideProgressBar()
    }

    fun setErrorView(errorMessage: String? = "", errorIcon: Int = R.drawable.app_logo) {
        val txtErrorMessage = mErrorViewBinding?.txtErrorMessage
        if (errorMessage.isNullOrEmpty().not()) {
            txtErrorMessage?.text = errorMessage
            txtErrorMessage?.show()
        } else {
            txtErrorMessage?.text = ""
            txtErrorMessage?.hide()
        }

        val ivErrorIcon = mErrorViewBinding?.ivErrorIcon
        if (errorIcon != 0) {
            ivErrorIcon?.setImageResource(errorIcon)
            ivErrorIcon?.show()
        } else {
            ivErrorIcon?.hide()
        }
    }

    fun showErrorView() {
        mErrorViewBinding?.layoutErrorView?.show()
    }

    fun hideErrorView() {
        mErrorViewBinding?.layoutErrorView?.hide()
    }

    fun captureBackPressState() {
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onSuperBackPressed()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        if (isProgressViewVisible()) {
            hideProgressBar()
        }
    }

    fun performBack() {
        findNavController().popBackStack()
    }

}
