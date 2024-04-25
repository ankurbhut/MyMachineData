package com.practical.mydatamachine.shared.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.dhaval2404.imagepicker.ImagePicker
import com.practical.mydatamachine.R
import com.practical.mydatamachine.application.Controller
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun ComponentActivity.launchWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

val Activity.screenWidth: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.width() - insets.left - insets.right
        } else {
            val view = window.decorView
            val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            resources.displayMetrics.widthPixels - insets.left - insets.right
        }
    }

fun Activity.isNotAdsActivity(): Boolean {
    Log.e(
        "ADTYPE", "${Controller.foregroundActivity?.localClassName}--${this.localClassName}"
    )
    return Controller.foregroundActivity == this
}

fun Activity.openAppByPackageName(packageNameToOpen: String?) {
    packageNameToOpen?.let {
        val startIntent = packageManager.getLaunchIntentForPackage(packageNameToOpen)
        if (startIntent != null) {
            startActivity(startIntent)
        }
    }
}

fun Activity.killAndRestartApp() {
    val ctx = baseContext
    val pm = ctx.packageManager
    val intent = pm.getLaunchIntentForPackage(ctx.packageName)
    val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
    ctx.startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

val Activity.screenHeight: Int
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            metrics.bounds.height() - insets.bottom - insets.top
        } else {
            val view = window.decorView
            val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            resources.displayMetrics.heightPixels - insets.bottom - insets.top
        }
    }

fun Activity.openAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}


fun Activity.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toastHtml(message: String?) {
    Toast.makeText(
        this,
        HtmlCompat.fromHtml("<big>$message</big>", HtmlCompat.FROM_HTML_MODE_LEGACY),
        Toast.LENGTH_SHORT
    ).show()
}

fun Activity.setNavigationBarColor(color: Int) {
    window?.let { it.navigationBarColor = color }
}

fun Activity.setStatusBarColor(color: Int) {
    window?.let { it.statusBarColor = color }
}

fun getFcmToken(callBack: (fcmToken: String, isSuccess: Boolean) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            callBack.invoke(
                task.exception?.localizedMessage ?: "Fetching FCM registration token failed", false
            )
            return@OnCompleteListener
        }
        val token = task.result
        if (token.isNullOrEmpty()) {
            callBack.invoke(
                task.exception?.localizedMessage ?: "Fetching FCM registration token failed", false
            )
            return@OnCompleteListener
        }
        Log.e("FCM_TOKEN", token)
        callBack.invoke(token, true)
    })

    fun Activity.setSecureActivity() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE
        )
    }

    fun Activity.keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    var animView: View? = null
    fun Activity.animateToast(
        icon: Int = R.drawable.app_logo,
        message: String?,
        type: Int = Toast.LENGTH_SHORT,
    ) {
        /*  if (animView?.parent != null) {
              animView?.hide()
              (animView?.parent as ViewGroup).removeView(animView)
          }

          val mToastBinding = LayoutAnimateToastBinding.inflate(layoutInflater)
          val parentView = findViewById<FrameLayout>(android.R.id.content)
          mToastBinding.toastIcon.setImageResource(icon)
          mToastBinding.toastMessage.text = message

          animView = mToastBinding.layoutContent
          ViewAnimatorUtil.animateToast(animView, type) { value ->
              when (value) {
                  1 -> {
                      mToastBinding.toastMessage.show()
                  }

                  2 -> {
                      mToastBinding.toastMessage.hide()
                  }

                  else -> {
                      parentView.removeView(mToastBinding.root)
                  }
              }
          }
          parentView.addView(mToastBinding.root)*/
    }
}

fun Activity.openMediaPicker(
    isCameraOnly: Boolean,
    isGalleryOnly: Boolean,
    activityLauncher: com.practical.mydatamachine.shared.base.BetterActivityResult<Intent, ActivityResult>?,
    listener: (fileUri: Uri?, thumbUri: Uri?) -> Unit
) {
    val builder = ImagePicker.with(this)
    if (isCameraOnly)
        builder.cameraOnly()
    if (isGalleryOnly)
        builder.galleryOnly()
    builder.crop()
        .maxResultSize(1440, 2160)
        .compress(100)
        .createIntent { intent ->
            activityLauncher?.launch(intent) { result ->
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val fileUri = data?.data
                        val thumbUri = Uri.parse(data?.getString(""))
                        listener.invoke(fileUri, thumbUri)
                    }

                    ImagePicker.RESULT_ERROR -> {
                        toast(ImagePicker.getError(data))
                    }

                    else -> {
                    }
                }
            }
        }
}

