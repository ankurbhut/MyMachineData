package com.practical.mydatamachine.utility

import android.os.Handler
import android.os.Looper
import java.util.concurrent.TimeUnit

class TimerCounterUtil(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
    private var mListeners: TimerUpdateListeners?,
) {
    private var millisUntilFinished: Long = 0
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null
    private var isRunning: Boolean = false
    private var hasFinished: Boolean = false

    init {
        initTimer()
    }

    private fun initTimer() {
        millisUntilFinished = millisInFuture
        mHandler = Handler(Looper.getMainLooper())
        mRunnable = Runnable {
            if (millisUntilFinished > 1000) {
                hasFinished = false
                val day = TimeUnit.DAYS.convert(millisUntilFinished, TimeUnit.MILLISECONDS)
                val hours =
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                    )
                val minutes =
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    )
                val seconds =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                mListeners?.onTimerUpdate(day, hours, minutes, seconds)
                mListeners?.onLeftTime(millisUntilFinished)
                millisUntilFinished -= countDownInterval
                if (isRunning) startCallBack()
            } else {
                if (hasFinished.not()) {
                    hasFinished = true
                    mListeners?.onTimerFinish()
                }
            }
        }
    }

    interface TimerUpdateListeners {
        fun onTimerUpdate(day: Long, hour: Long, minutes: Long, seconds: Long)
        fun onLeftTime(timeLeft: Long) {}
        fun onTimerFinish()
    }

    private fun removeCallBack() {
        mRunnable?.let { mHandler?.removeCallbacks(it) }
    }

    private fun startCallBack() {
        mRunnable?.let { mHandler?.postDelayed(it, countDownInterval) }
    }


    fun setTimerUpdateListener(mListeners: TimerUpdateListeners) {
        this.mListeners = mListeners
    }


    fun destroyTimer() {
        isRunning = false
        removeCallBack()
    }

    fun finishTimer() {
        mListeners?.onTimerFinish()
        destroyTimer()
    }

    fun pauseTimer() {
        isRunning = false
        removeCallBack()
    }

    fun resumeTimer() {
        isRunning = true
        removeCallBack()
        startCallBack()
    }

    fun startTimer() {
        removeCallBack()
        isRunning = true
        millisUntilFinished = millisInFuture
        startCallBack()
    }

    fun getCurrentTimestamp():Long{
        return millisUntilFinished
    }

    fun updateTime(updateTime: Long) {
        millisUntilFinished = updateTime
    }

    fun isTimerRunning():Boolean{
        return isRunning
    }
}