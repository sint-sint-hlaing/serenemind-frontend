package com.serenemind.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FocusViewModel : ViewModel() {
    private val _timeLeft = MutableStateFlow(25 * 60)
    val timeLeft = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _mode = MutableStateFlow(FocusMode.POMODORO)
    val mode = _mode.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
        }
    }

    private fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun setMode(mode: FocusMode) {
        this._mode.value = mode
        _timeLeft.value = mode.durationMinutes * 60
        pauseTimer()
    }

    fun reset() {
        _timeLeft.value = _mode.value.durationMinutes * 60
        pauseTimer()
    }
}

enum class FocusMode(val title: String, val durationMinutes: Int) {
    POMODORO("Pomodoro", 25),
    SHORT_BREAK("Short Break", 5),
    LONG_BREAK("Long Break", 15)
}
