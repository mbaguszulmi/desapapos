package com.desapabandara.pos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class NumPadViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val numberType: NumberType,
    private val numberFormatter: ((Number) -> String)?,
    private val stringFormatter: ((String) -> String)?,
    private val maxLength: Int = 0
): ViewModel() {
    private val _floatValue = MutableStateFlow(0)
    private val _number = MutableStateFlow(0)
    protected val _text = MutableStateFlow("")
    private var dotReached = false

    protected val numberCombined = when (numberType) {
        NumberType.Integer -> _number
        NumberType.Decimal -> combine(_number, _floatValue) { number, floatValue ->
            number + (".$floatValue".toDouble())
        }
        NumberType.IntText -> _text.map {
            if (it.isBlank()) {
                0
            } else {
                it.toInt()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0 as Number)

    protected val numberStr = numberCombined.map {
        it.toString()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val numberDisplay = if (numberType == NumberType.IntText) {
        _text.map {
            stringFormatter?.invoke(it) ?: it
        }
    } else {
        numberCombined.map {
            numberFormatter?.invoke(it) ?: it.toString()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    private fun getCurrentLength(): Int {
        return if (numberType == NumberType.IntText) {
            _text.value.length
        } else {
            numberCombined.value.toString().length
        }
    }

    fun tapInt(number: Int) {
        viewModelScope.launch(ioDispatcher) {
            val currentLength = getCurrentLength()

            if (maxLength in 1..currentLength) {
                return@launch
            }

            if (dotReached) {
                val numStr = _floatValue.value.toString() + number.toString()
                _floatValue.value = numStr.toDouble().toInt()
            } else {
                if (numberType == NumberType.IntText) {
                    _text.value += number.toString()
                    return@launch
                }

                val numStr = _number.value.toString() + number.toString()
                _number.value = numStr.toInt()
            }
        }
    }

    fun tapStrToInt(number: String) {
        viewModelScope.launch(ioDispatcher) {
            val currentLength = getCurrentLength()

            if (maxLength in 1..currentLength) {
                return@launch
            }

            try {
                if (dotReached) {
                    val numStr = _floatValue.value.toString() + number
                    _floatValue.value = numStr.toInt()
                } else {
                    if (numberType == NumberType.IntText) {
                        _text.value += number
                        return@launch
                    }

                    val numStr = _number.value.toString() + number
                    _number.value = numStr.toInt()
                }
            } catch (e: Throwable) {
                Timber.e("Error when converting to int")
            }
        }
    }

    fun tapDot() {
        viewModelScope.launch(ioDispatcher) {
            if (!dotReached) {
                dotReached = true
            }
        }
    }

    fun del() {
        viewModelScope.launch(ioDispatcher) {
            if (dotReached) {
                val floatStr = _floatValue.value.toString()
                if (floatStr.length == 1) {
                    dotReached = false
                    _floatValue.value = 0
                } else {
                    _floatValue.value = floatStr.dropLast(1).toInt()
                }
            } else {
                if (numberType == NumberType.IntText) {
                    _text.value = _text.value.dropLast(1)
                    return@launch
                }

                val numStr = _number.value.toString().dropLast(1)
                _number.value = numStr.ifBlank { "0" }.toInt()
            }
        }
    }

    fun clear() {
        viewModelScope.launch(ioDispatcher) {
            _floatValue.value = 0
            _number.value = 0
            _text.value = ""
            dotReached = false
        }
    }

    fun setInitialNumber(number: Number) {
        viewModelScope.launch(ioDispatcher) {
            when (numberType) {
                NumberType.Integer -> {
                    _number.value = number.toInt()
                }
                NumberType.Decimal -> {
                    val parts = number.toString().split(".")
                    _number.value = parts[0].toInt()
                    if (parts.size > 1) {
                        _floatValue.value = parts[1].toInt()
                        if (_floatValue.value > 0) {
                            dotReached = true
                        }
                    }
                }
                NumberType.IntText -> {
                    _text.value = number.toInt().toString()
                }
            }
        }
    }

    abstract fun confirm()
}

enum class NumberType {
    Integer,
    Decimal,
    IntText
}
