package com.practical.mydatamachine.shared.base.viewbase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.practical.mydatamachine.R
import com.practical.mydatamachine.databinding.LayoutLoadingViewBinding
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.ViewIntent
import com.practical.mydatamachine.shared.base.ViewState
import com.practical.mydatamachine.shared.callback.IViewRenderer
import com.practical.mydatamachine.shared.extension.hide
import com.practical.mydatamachine.shared.extension.isVisible
import com.practical.mydatamachine.shared.extension.openAppSetting
import com.practical.mydatamachine.shared.extension.show
import com.practical.mydatamachine.shared.extension.showAlertDialog
import com.practical.mydatamachine.shared.extension.toast
import com.practical.mydatamachine.shared.extension.viewModelProvider
import com.practical.mydatamachine.utility.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class AdvancedBaseActivity<VDB : ViewDataBinding, INTENT : ViewIntent, STATE : ViewState, VM : AdvanceBaseViewModel<INTENT, STATE>>(
    val bindingFactory: (LayoutInflater) -> VDB,
    private val modelClass: Class<VM>,
) : AppCompatActivity(), IViewRenderer<STATE>, CoroutineScope by CoroutineScope(Dispatchers.Main) {

    protected val TAG: String = this::class.java.simpleName

    private var _binding: VDB? = null
    protected val binding get() = _binding
    private var mLoadingViewBinding: LayoutLoadingViewBinding? = null

    private lateinit var viewState: STATE
    protected val mState get() = viewState
    protected var mViewModel: VM? = null
    private fun getViewModelClass(): KClass<VM> =
        ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>).kotlin


    private var mSnackBar: Snackbar? = null
    private var mOrientation: Int = Configuration.ORIENTATION_PORTRAIT
    protected var mActivityLauncher: com.practical.mydatamachine.shared.base.BetterActivityResult<Intent, ActivityResult>? = null
    open fun toggleOfferBubbleView(isShow: Boolean? = false) {}
    open fun onUserInfoUpdate() {}
    open fun onSuperBackPressed() {}
    open fun changeBottomNavItem(itemId: Int) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(binding?.root)
        mOrientation = resources.configuration.orientation
        setBackPressDispatcher()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setLoadingView(view)
        initObjects()
        initUI()
        initDATA()
        setVariables()
        lifecycleScope.launch {
            mViewModel?._state?.collectLatest {
                render(it)
            }
        }
    }


    /*   fun navigate(destination: NavDirections) = with(findNavController(R.id.fragmentContainerView)) {
           currentDestination?.getAction(destination.actionId)
               ?.let { navigate(destination) }
       }
   */

    private fun initObjects() {
        mActivityLauncher = com.practical.mydatamachine.shared.base.BetterActivityResult.registerActivityForResult(this)
        mViewModel =
            viewModelProvider(ViewModelProvider.AndroidViewModelFactory(), modelClass.kotlin)
    }

    private fun setLoadingView(view: View?) {
        runOnUiThread {
            view?.let {
                if (it.findViewById<RelativeLayout>(R.id.layoutAppLoaderView) != null) {
                    mLoadingViewBinding = LayoutLoadingViewBinding.bind(it)
                }
            }
        }
    }

    abstract fun initDATA()

    abstract fun initUI()

    open fun setVariables() {}

    open fun showAppRatingDialog() {}

    fun isLandscapeMode(): Boolean = mOrientation == Configuration.ORIENTATION_LANDSCAPE

    fun isPortraitMode(): Boolean = mOrientation == Configuration.ORIENTATION_PORTRAIT

    fun clearFullScreenActivity() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    }

    fun showSnackBar(view: View?, message: String?) {
        if (view == null) return
        if (message.isNullOrEmpty()) return
        if (mSnackBar != null && mSnackBar!!.isShownOrQueued) mSnackBar?.dismiss()
        mSnackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        mSnackBar?.show()
    }

    open fun recreateActivity() {
        startActivity(intent)
        finish()
    }

    fun hideKeyBoard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    fun getContainerView(): View? {
        return binding?.root
    }

    private fun setBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onSuperBackPressed()
            }
        })
    }

    private var mBackCounter = 0
    open fun appExitAlert() {
        if (mBackCounter == 0) toast(getString(R.string.app_exit_message))
        mBackCounter++
        Util.executeDelay(2500) { mBackCounter = 0 }
        if (mBackCounter > 1) finish()
    }

    open fun triggerNotificationPermission() {
        intimateUserForNotificationPermission()
    }

    private fun intimateUserForNotificationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.e(TAG, "onCreate: PERMISSION GRANTED")
                showAppRatingDialog()
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                showNotificationPermissionNotGrantedDialog()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    showAppRatingDialog()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                showNotificationPermissionNotGrantedDialog()
            } else {
                showAppRatingDialog()
            }
        }


    private fun showNotificationPermissionNotGrantedDialog() =
        if (isFinishing.not()) showAlertDialog(title = getString(R.string.required_permission),
            message = getString(R.string.notification_permission_message),
            positiveRes = R.string.ok,
            positiveClick = {
                openAppSetting()
                showAppRatingDialog()
            }).show()
        else {
        }


    fun showProgressBar(lottieFile: Int = 0, message: String? = "") {
        mLoadingViewBinding?.layoutAppLoaderView?.show()
        mLoadingViewBinding?.lottieAnimationView?.resumeAnimation()
        if (message.isNullOrEmpty().not()) {
            mLoadingViewBinding?.txtErrorMessage?.text = message
            mLoadingViewBinding?.txtErrorMessage?.show()
        } else {
            mLoadingViewBinding?.txtErrorMessage?.hide()
        }
    }

    fun isProgressVisible(): Boolean {
        return mLoadingViewBinding?.layoutAppLoaderView?.isVisible() == true
    }

    fun hideProgressBar() {
        mLoadingViewBinding?.lottieAnimationView?.pauseAnimation()
        mLoadingViewBinding?.layoutAppLoaderView?.hide()
    }
}