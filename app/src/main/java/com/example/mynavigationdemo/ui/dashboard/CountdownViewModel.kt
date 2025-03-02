package com.example.mynavigationdemo.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.CountDownTimer
import android.content.res.Configuration

class CountdownViewModel : ViewModel() {
    private var timeLeftWhenPaused: Long = 0
    private var countDownTimer: CountDownTimer? = null
    private val _timeRemaining = MutableLiveData<Long>()
    val timeRemaining: LiveData<Long> get() = _timeRemaining
    private val _isFinished = MutableLiveData<Boolean>()
    val isFinished: LiveData<Boolean> get() = _isFinished
    private val _timeRemainingLandscape = MutableLiveData<Long>()
    val timeRemainingLandscape: LiveData<Long> get() = _timeRemainingLandscape

    init {
        _isFinished.value = false
    }

    fun startCountdown(timeInMillis: Long) {
        countDownTimer?.cancel()
        _isFinished.value = false
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timeRemaining.value = millisUntilFinished
                timeLeftWhenPaused = millisUntilFinished
            }

            override fun onFinish() {
                _isFinished.value = true
            }
        }.start()
    }

    fun stopCountdown() {
        countDownTimer?.cancel()
        _timeRemaining.value = 0
        _isFinished.value = true
        timeLeftWhenPaused = 0
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
