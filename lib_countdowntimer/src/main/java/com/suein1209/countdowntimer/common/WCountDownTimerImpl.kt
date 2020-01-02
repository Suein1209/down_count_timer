package com.suein1209.countdowntimer.common

/**
 * 외부 모듈 사용 interface
 *
 * Created by suein1209
 */
interface WCountDownTimerImpl {
    /**
     * Timer를 시작한다.
     */
    fun start(milliSec: Long, onOverTime: (() -> Unit)? = null)

    /**
     * Timer를 취소한다.
     * - 기존에 실행이 안되었다면 아무 행동도 하지 않는다.
     */
    fun cancel()
}