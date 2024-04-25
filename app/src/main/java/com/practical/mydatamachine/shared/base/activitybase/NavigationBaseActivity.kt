package com.practical.mydatamachine.shared.base.activitybase

import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.practical.mydatamachine.shared.base.AdvanceBaseViewModel
import com.practical.mydatamachine.shared.base.ViewIntent
import com.practical.mydatamachine.shared.base.ViewState
import com.practical.mydatamachine.shared.base.viewbase.AdvancedBaseActivity
import com.practical.mydatamachine.ui.post.PostDetailActivity

abstract class NavigationBaseActivity<VDB : ViewDataBinding, INTENT : ViewIntent, STATE : ViewState,
        VM : AdvanceBaseViewModel<INTENT, STATE>>(
    bindingFactory: (LayoutInflater) -> VDB,
    modelClass: Class<VM>,
) : AdvancedBaseActivity<VDB, INTENT, STATE, VM>(bindingFactory, modelClass) {

    fun openPostDetailActivity(postId: Int) {
        val intent = Intent(this, PostDetailActivity::class.java)
        intent.putExtra("post_id", postId)
        startActivity(intent)
    }

    companion object {
        const val REQUEST_PROFILE = 1
        const val REQUEST_DEFAULT = 0
        const val REQUEST_TUTORIAL = 2

        const val INTENT_KEY: String = "intent_type"
        const val FORCE_REFRESH_HOME: Int = 7

        const val RESULT_TYPE = "resultType"
        const val PROFILE_EDITED = "profileEdited"
        const val SAVE_N_START_OVERLAY = "saveAndStartOverlay"
        const val RESULT_KEY = "fragmentResult"
    }
}