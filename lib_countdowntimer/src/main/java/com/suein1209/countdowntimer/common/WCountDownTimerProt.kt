package com.suein1209.countdowntimer.common

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import java.util.concurrent.TimeUnit

/**
 * Timer 공통으로 사용되는 기능
 *
 * Created by suein1209
 */
internal interface WCountDownTimerProt {

    /**
     * 밀리초를 int 시,분,초 로 반환한다.
     */
    fun convertTime(millis: Long, onTickCallback: (hour: Int, minute: Int, second: Int) -> Unit) {
        val hour = TimeUnit.MILLISECONDS.toHours(millis)
        val minute = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millis)
        )
        val second = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millis)
        )
        onTickCallback(hour.toInt(), minute.toInt(), second.toInt())
    }

    /**
     * 밀리초를 String 시(십의자리, 일의자리), 분(십의자리, 일의자리), 초(십의자리, 일의자리)로 반환한다.
     */
    fun convertTimerSeveralStringFormat(millis: Long, timeFormatCallback: (hourTensPosStr: String, hourUnitPosStr: String, minuteTensPosStr: String, minuteUnitPosStr: String, secondTensPosStr: String, secondUnitPosStr: String) -> Unit) {
        convertTime(millis) { hour, minute, second ->
            timeFormatCallback(
                    String.format("%01d", hour / 10),
                    String.format("%01d", hour % 10),
                    String.format("%01d", minute / 10),
                    String.format("%01d", minute % 10),
                    String.format("%01d", second / 10),
                    String.format("%01d", second % 10)
            )
        }
    }

    /**
     * 밀리초를 Int 시(십의자리, 일의자리), 분(십의자리, 일의자리), 초(십의자리, 일의자리)로 반환한다.
     */
    fun convertTimerToSeveralDigit(millis: Long, onTickCallback: (hourTensPos: Int, hourUnitPos: Int, minuteTensPos: Int, minuteUnitPos: Int, secondTensPos: Int, secondUnitPos: Int) -> Unit) {
        convertTime(millis) { hour, minute, second ->
            onTickCallback(hour / 10, hour % 10, minute / 10, minute % 10, second / 10, second % 10)
        }
    }

    /**
     * xml을 통해 설정된(interpolator) 애니메이션이 설정된 [Animation]을 반환한다.
     */
    fun Context.createAnim(animResId: Int, interpolator: Int = 0): Animation {
        val mContext = this
        return AnimationUtils.loadAnimation(mContext, animResId).apply {
            if (interpolator != 0) {
                setInterpolator(mContext, interpolator)
            }
        }
    }
}
