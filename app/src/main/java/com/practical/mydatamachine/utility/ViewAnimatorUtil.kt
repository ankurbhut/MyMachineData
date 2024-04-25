package com.practical.mydatamachine.utility

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

object ViewAnimatorUtil {

    fun setCoinAnimation(
        animView: View?, parentView: ConstraintLayout, callback: (view: View) -> Unit
    ) {
        setCoinScaleAnimation(animView, parentView) {
            callback.invoke(it)
        }
    }

    private fun setCoinScaleAnimation(
        animView: View?, parentView: ConstraintLayout, callback: (view: View) -> Unit
    ) {
        animView?.let { view ->
            view.animate().scaleY(2.30f).scaleX(2.30f).translationX(view.x)
                .translationY(view.y - 40).setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(anim: Animator) {

                    }

                    override fun onAnimationEnd(anim: Animator) {
                        anim.cancel()
                        setTranslateAnimation(view, parentView) {
                            callback.invoke(it)
                        }
                    }

                    override fun onAnimationCancel(anim: Animator) {

                    }

                    override fun onAnimationRepeat(anim: Animator) {

                    }
                }).duration = 450
        }
    }

    private fun setTranslateAnimation(
        animView: View?, parentView: ConstraintLayout, callback: (view: View) -> Unit
    ) {
        animView?.let { view ->
            val valueX = animView.x
            val valueY = parentView.top.minus(50)
            view.animate().translationX(valueX).translationY(valueY.toFloat())
                .scaleY(0.50f).scaleX(0.50f).alpha(0.25f).setStartDelay(1000)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(anim: Animator) {
                    }

                    override fun onAnimationEnd(anim: Animator) {
                        anim.cancel()
                        view.clearAnimation()
                        parentView.removeView(view)
                        callback.invoke(view)
                    }

                    override fun onAnimationCancel(anim: Animator) {

                    }

                    override fun onAnimationRepeat(anim: Animator) {

                    }
                }).duration = 1200
        }
    }

    fun animateToast(animView: View?, type: Int, callBack: (Int) -> Unit) {
        animView?.let {
            val delay = 300L
            it.animate().scaleY(1f).scaleX(1f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    it.postDelayed({
                        callBack.invoke(1)
                        it.postDelayed({
                            callBack.invoke(2)
                            it.postDelayed({
                                it.animate().scaleY(0f).scaleX(0f)
                                    .setListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animator: Animator) {

                                        }

                                        override fun onAnimationEnd(animator: Animator) {
                                            callBack.invoke(3)
                                        }

                                        override fun onAnimationCancel(animator: Animator) {
                                            callBack.invoke(3)
                                        }

                                        override fun onAnimationRepeat(animator: Animator) {
                                        }
                                    }).setDuration(delay.minus(100)).start()

                            }, delay)
                        }, if (type == Toast.LENGTH_SHORT) 3500 else 5500)
                    }, delay)
                }

                override fun onAnimationCancel(animator: Animator) {
                    callBack.invoke(3)
                }

                override fun onAnimationRepeat(animator: Animator) {
                }
            }).setDuration(delay.minus(100)).start()
        }
    }

    fun animateTextView(initialValue: Int, finalValue: Int, textView: TextView?) {
        val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
        valueAnimator.duration = 1500 // 5 sec
        valueAnimator.addUpdateListener { data ->
            textView?.let {
                //BindingAdaptersUtil.setNumberPattern(it, valueAnimator.animatedValue.toString())
                it.text = data.animatedValue.toString()
                it.requestLayout()
            }
        }
        valueAnimator.start()
    }
}