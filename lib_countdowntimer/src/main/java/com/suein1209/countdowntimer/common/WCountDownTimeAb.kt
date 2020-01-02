package com.suein1209.countdowntimer.common

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import com.suein1209.countdowntimer.R
import com.suein1209.countdowntimer.common.util.checkNotNullSafety

/**
 * CountDownTimer Parent
 * Image CountDownTimer 와 Text CountDownTimer 의 공통 적으로 사용되는 기능의 모음
 *
 * Created by suein1209
 */
abstract class WCountDownTimeAb<T : View> : LinearLayout, WCountDownTimerProt, WCountDownTimerImpl {

    /**
     * 시간 십의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_HOUR_TENS = 0

    /**
     * 시간 일의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_HOUR_UNIT = 1

    /**
     * 분 십의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_MINUTE_TENS = 2

    /**
     * 분 일의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_MINUTE_UNIT = 3

    /**
     * 초 십의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_SECOND_TENS = 4

    /**
     * 초 일의 자리
     */
    @Suppress("PropertyName")
    internal val POSITION_SECOND_UNIT = 5

    /**
     * 포지션별 타입 Listing
     * - 초기화 및 기본 세팅시 편의를 위히 만듬
     */
    protected val positionTypeArray = mutableListOf<Int>().apply {
        add(0, POSITION_HOUR_TENS)
        add(1, POSITION_HOUR_UNIT)
        add(2, POSITION_MINUTE_TENS)
        add(3, POSITION_MINUTE_UNIT)
        add(4, POSITION_SECOND_TENS)
        add(5, POSITION_SECOND_UNIT)
    }

    /**
     * 각 Digit 포지션(십의 자리 혹은 일의자리) 마다 아래에서 위로 올라오는 애니메이션을 주기위해
     * 위 아래 같은 View 한벌씩이 필요하다. 그 View을 저장하기 위해 사용되는 Key
     * - TOP : 현재 위치에서 위로 올라가는 View Key
     * - BELOW : 아래에서 현재 위치로 올라오는 View Key
     */
    enum class DigitPosition {
        TOP, BELOW
    }

    /**
     * 뷰를 단순히 저장해둔 Map
     */
    internal val viewMap = mutableMapOf<Int, MutableMap<DigitPosition, T>>()

    /**
     * 이전에 저장된 시간
     * - 새로운 시간 틱(Tick)가 발생된 뒤 애니메이션을 주기위해서는 이전의 값과 비교해야 한다.
     *   비교를 위한 이전 값 저장.
     */
    internal val prevTimeMap = mutableMapOf<Int, Int>()

    /**
     * CountDownTimer
     */
    private var wCountDownTimer: CountDownTimer? = null

    /**
     * xml을 통해 전달 된 애니메이션
     * - 기본 값으로 android.R.anim.linear_interpolator
     */
    var interpolator: Int = 0

    /**
     * 상속 받은 자식 클래스에서 사용되는 View(Inflate된)를 반환한다.
     */
    internal abstract fun initLayout(): View

    /**
     * Init 이후 후 처리를 위한 메소드
     * - 다소 억지스럽게 만든 메소드이다. [initLayout]메소드에서 뷰를 초기화(Map에 설정) 해주게 되는데
     *   이 이후 상속받은 자식 클래스에서 설정을 하기위한 용도이다.
     */
    internal abstract fun afterInitLayout(attrs: AttributeSet? = null, defStyleAttr: Int? = null)

    /**
     * Tick 발생시 자식 클래스에 전달되는 abstract 메소드
     */
    internal abstract fun onChangedTimes(onChangedTimeList: List<Pair<Int, Int>>, onNotChangedTimeList: List<Int>)

    //@formatter:off
    /* [START] constructor */
    constructor(context: Context?) : super(context) { initViewResource(this.initLayout()); this.afterInitLayout() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initViewResource(this.initLayout()); this.afterInitLayout(attrs, null) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initViewResource(this.initLayout()); this.afterInitLayout(attrs, defStyleAttr) }
    /* [END] constructor */
    //@formatter:on

    /**
     * 설정된 View Resource 를 Map에 저장한다.
     * - 상속 받은 Image, Text 형의 Layout에 동일한 이름으로 저장되어 있다.(...꼼...수...)
     */
    private fun initViewResource(view: View) {
        viewMap[POSITION_HOUR_TENS] = mutableMapOf()
        viewMap[POSITION_HOUR_TENS]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_tens_pos_hours)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_tens_pos_hours)
        }

        viewMap[POSITION_HOUR_UNIT] = mutableMapOf()
        viewMap[POSITION_HOUR_UNIT]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_unit_pos_hours)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_unit_pos_hours)
        }

        viewMap[POSITION_MINUTE_TENS] = mutableMapOf()
        viewMap[POSITION_MINUTE_TENS]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_tens_pos_minute)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_tens_pos_minute)
        }

        viewMap[POSITION_MINUTE_UNIT] = mutableMapOf()
        viewMap[POSITION_MINUTE_UNIT]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_unit_pos_minute)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_unit_pos_minute)
        }

        viewMap[POSITION_SECOND_TENS] = mutableMapOf()
        viewMap[POSITION_SECOND_TENS]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_tens_pos_second)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_tens_pos_second)
        }

        viewMap[POSITION_SECOND_UNIT] = mutableMapOf()
        viewMap[POSITION_SECOND_UNIT]!!.run {
            this[DigitPosition.TOP] = view.findViewById(R.id.top_unit_pos_second)
            this[DigitPosition.BELOW] = view.findViewById(R.id.below_unit_pos_second)
        }
    }

    /**
     * 이전 설정된 시간 값과 비교해서 차이가 있는 부분만 List로 반환한다.
     */
    private fun getChangedPosInfo(milliSec: Long): Pair<MutableList<Pair<Int, Int>>, MutableList<Int>> {
        val ret = Pair<MutableList<Pair<Int, Int>>, MutableList<Int>>(mutableListOf(), mutableListOf())
        convertTimerToSeveralDigit(milliSec) { hourTensPosDigit, hourUnitPosDigit, minuteTensPosDigit, minuteUnitPosDigit, secondTensPosDigit, secondUnitPosDigit ->
            if (prevTimeMap[POSITION_HOUR_TENS] != hourTensPosDigit) {
                ret.first.add((POSITION_HOUR_TENS to hourTensPosDigit))
            } else {
                ret.second.add(POSITION_HOUR_TENS)
            }
            if (prevTimeMap[POSITION_HOUR_UNIT] != hourUnitPosDigit) {
                ret.first.add((POSITION_HOUR_UNIT to hourUnitPosDigit))
            } else {
                ret.second.add(POSITION_HOUR_UNIT)
            }
            if (prevTimeMap[POSITION_MINUTE_TENS] != minuteTensPosDigit) {
                ret.first.add((POSITION_MINUTE_TENS to minuteTensPosDigit))
            } else {
                ret.second.add(POSITION_MINUTE_TENS)
            }
            if (prevTimeMap[POSITION_MINUTE_UNIT] != minuteUnitPosDigit) {
                ret.first.add((POSITION_MINUTE_UNIT to minuteUnitPosDigit))
            } else {
                ret.second.add(POSITION_MINUTE_UNIT)
            }
            if (prevTimeMap[POSITION_SECOND_TENS] != secondTensPosDigit) {
                ret.first.add((POSITION_SECOND_TENS to secondTensPosDigit))
            } else {
                ret.second.add(POSITION_SECOND_TENS)
            }
            if (prevTimeMap[POSITION_SECOND_UNIT] != secondUnitPosDigit) {
                ret.first.add((POSITION_SECOND_UNIT to secondUnitPosDigit))
            } else {
                ret.second.add(POSITION_SECOND_UNIT)
            }
        }
        return ret

    }

    /**
     * 시간(CountDown)이 끝나고 호출되는 CallBack
     */
    private var onOverTime: (() -> Unit)? = null

    /**
     * Count Down Timer를 시작한다.
     * - CountDownTimer가 기존에 생성되어 있다면 cancel 후 오브젝트를 새로 생성해 한다.
     * - 오브젝트는를 만약 cancel 후 다시 start 하게 되면 초기 설정된 시간으로 다시 시작된다.
     */
    protected fun startCountDownTimer(milliSec: Long, onOverTime: (() -> Unit)? = null) {
        onOverTime?.let {
            this.onOverTime = it
        }

        convertTimerToSeveralDigit(milliSec) { hourTensPosDigit, hourUnitPosDigit, minuteTensPosDigit, minuteUnitPosDigit, secondTensPosDigit, secondUnitPosDigit ->
            prevTimeMap[POSITION_HOUR_TENS] = hourTensPosDigit
            prevTimeMap[POSITION_HOUR_UNIT] = hourUnitPosDigit

            prevTimeMap[POSITION_MINUTE_TENS] = minuteTensPosDigit
            prevTimeMap[POSITION_MINUTE_UNIT] = minuteUnitPosDigit

            prevTimeMap[POSITION_SECOND_TENS] = secondTensPosDigit
            prevTimeMap[POSITION_SECOND_UNIT] = secondUnitPosDigit
        }

        if (wCountDownTimer != null) {
            wCountDownTimer!!.cancel()
        }
        wCountDownTimer = createCountDownTimer(milliSec)
        wCountDownTimer!!.start()
    }

    /**
     * CountDownTimer를 새로 생성한다.
     */
    private fun createCountDownTimer(milliSec: Long): CountDownTimer {
        return object : CountDownTimer(milliSec, 1000) {
            override fun onTick(milli: Long) {

                var changedTimeList: MutableList<Pair<Int, Int>>
                var notChangedTimeList: MutableList<Int>
                getChangedPosInfo(milli).let {
                    changedTimeList = it.first
                    notChangedTimeList = it.second
                }

                //하위 자식에 변화된 위치와, 변화하지 않는 위치의 list를 전달한다.
                onChangedTimes(changedTimeList, notChangedTimeList)

                //변환된 값을 이전 Time Map에 저장한다.
                initPrevMap(changedTimeList)
            }

            override fun onFinish() {
                onOverTime?.invoke()
            }
        }
    }

    /**
     * 변환된 값을 이전 Time Map에 저장한다.
     */
    private fun initPrevMap(changedTimeList: MutableList<Pair<Int, Int>>) {
        changedTimeList.forEach {
            prevTimeMap[it.first] = it.second
        }
    }

    /**
     * Count Down Timer를 취소한다.
     */
    override fun cancel() {
        wCountDownTimer?.cancel()
        wCountDownTimer = null
    }

    /**
     * Timer가 활성화 되어 있는지 체크한다.
     */
    fun isActivatedTimer() = wCountDownTimer != null

    /**
     * 애니메이션 오브젝트를 저장한 MAP
     * - 애니메이션을 설정할때마다 새로 생성하는 것이 아닌 해당 하는 뷰에 한번 설정했다면 정장해 두었다가 사용한다.
     *   현재 로직상에서는 초(십과 일의자리), 분(일의자리)만 캐싱해서 사용하고 나머지는 사용할때 마다 생성한다.
     *   자주 사용되지 않는 오브젝트를 메모리에 남겨두지 않기 위함이다.
     */
    private val digitAnimMap = mutableMapOf<Int, MutableMap<DigitPosition, Animation>>()

    /**
     * 숫자 애니메이션을 시작한다.
     */
    protected fun startDigitAnim(positionType: Int, digitPosition: DigitPosition) {
        val anim = if (isNeedCreateAmin(positionType, digitPosition)) {

            checkNotNullSafety(digitAnimMap[positionType]) {
                digitAnimMap[positionType] = mutableMapOf()
            }

            //없거나 자주 사용되지 않는 위치의 오브젝트이면 새로 생성한다.
            val tempAnim = context.createAnim(if (digitPosition == DigitPosition.TOP) R.anim.anim1_up_to_current else R.anim.anim1_current_to_down, interpolator)
            digitAnimMap[positionType]!![digitPosition] = tempAnim
            tempAnim

        } else {
            //기존에 저장돼 있다면 사용
            digitAnimMap[positionType]!![digitPosition]
        }

        //애니메이션을 시작한다.
        viewMap[positionType]?.run {
            this[digitPosition]!!.startAnimation(anim)
        }
    }

    /**
     * 애내메이션 오브젝트를 새로 시작해야 하는지 확인한다.
     * - 기존에 저장되어 있지 않은경우,
     * - 시(십의자리와 일의자리), 분(십의자리)의 자리 일경우
     */
    private fun isNeedCreateAmin(positionType: Int, digitPosition: DigitPosition) = digitAnimMap[positionType]?.get(digitPosition) == null
            || positionType == POSITION_HOUR_TENS
            || positionType == POSITION_HOUR_UNIT
            || positionType == POSITION_MINUTE_TENS
}