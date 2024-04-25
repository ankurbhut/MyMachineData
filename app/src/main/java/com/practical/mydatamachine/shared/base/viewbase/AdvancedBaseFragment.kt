package com.practical.mydatamachine.shared.base.viewbase

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.ViewIntent
import com.practical.mydatamachine.shared.base.ViewState
import com.practical.mydatamachine.shared.callback.IViewRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

abstract class AdvancedBaseFragment<VDB : ViewDataBinding, INTENT : ViewIntent, STATE : ViewState,
        VM : AdvanceBaseViewModel<INTENT, STATE>>(
    bindingFactory: (LayoutInflater) -> VDB,
    private val modelClass: Class<VM>,
    private val isUseParentViewModel: Boolean = false
) : BaseFragment<VDB>(bindingFactory), IViewRenderer<STATE>,
    CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private lateinit var viewState: STATE
    private lateinit var event: INTENT

    protected val mState get() = viewState

    protected val mEvent get() = event

    protected val mViewModel: VM by lazy {
          ViewModelProvider(
             if (isUseParentViewModel) requireParentFragment()
             else this,
              SavedStateViewModelFactory(requireActivity().application, if (isUseParentViewModel) requireParentFragment() else this)
         )[modelClass.kotlin.java]
    }

    private fun getViewModelClass(): KClass<VM> =
        ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>).kotlin


    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }
}