package com.suein1209.countdowntimer.common.util

internal inline fun <T : Any> checkNotNullSafety(value: T?, nullCallBack: () -> Unit): T? {
    return if (value == null) {
        nullCallBack.invoke()
        null
    } else {
        value
    }
}