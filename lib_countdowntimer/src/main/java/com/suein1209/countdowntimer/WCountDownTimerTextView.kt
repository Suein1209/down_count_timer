package com.suein1209.countdowntimer

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.suein1209.countdowntimer.common.WCountDownTimeAb

/**
 * Text Count Down Timer
 *
 * Created by suein1209
 */
class WCountDownTimerTextView : WCountDownTimeAb<TextView> {
    //@formatter:off
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    //@formatter:on

    lateinit var colon1: TextView
    lateinit var colon2: TextView

    /**
     * init
     */
    override fun initLayout(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.view_textview, this, true)
        colon1 = view.findViewById(R.id.colon1)
        colon2 = view.findViewById(R.id.colon2)
        return view
    }

    /**
     * after init
     * - xml에서 설정한 값들을 세팅한다.
     */
    override fun afterInitLayout(attrs: AttributeSet?, defStyleAttr: Int?) {
        attrs?.let {
            val attr =
                context.obtainStyledAttributes(attrs, R.styleable.WCountDownTimerTextView, 0, 0)

            //TextSize 세팅
            val size =
                attr.getDimensionPixelSize(R.styleable.WCountDownTimerTextView_digitTextSize, 0)
            if (size != 0) {
                initTextSize(size.toFloat())
            }

            //TextColor 세팅
            val color = attr.getColor(R.styleable.WCountDownTimerTextView_digitTextColor, 0)
            if (color != 0) {
                initTextColor(color)
            }

            //Animation interpolator
            interpolator = attr.getResourceId(
                R.styleable.WCountDownTimerImageView_digitImgAnim,
                android.R.anim.linear_interpolator
            )
        }
    }

    /**
     * Text Size 세팅
     */
    private fun initTextSize(size: Float) {
        colon1.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        colon2.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)

        positionTypeArray.forEach { positionType ->
            viewMap[positionType]!!.run {
                this[DigitPosition.TOP]!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
                this[DigitPosition.BELOW]!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            }
        }
    }

    /**
     * Text Color 세팅
     */
    private fun initTextColor(color: Int) {
        colon1.setTextColor(color)
        colon2.setTextColor(color)
        positionTypeArray.forEach { positionType ->
            viewMap[positionType]!!.run {
                this[DigitPosition.TOP]!!.setTextColor(color)
                this[DigitPosition.BELOW]!!.setTextColor(color)
            }
        }
    }

    /**
     * DownCountTimer(with Animation)를 시작한다.
     * - 만약 설정되는 밀리초가 0 이라면 00:00:00으로 세팅만 하고 timer는 시작하지 않는다.
     */
    override fun start(milliSec: Long, onOverTime: (() -> Unit)?) {
        //init 설정
        convertTimerSeveralStringFormat(milliSec) { hourTensPosStr, hourUnitPosStr, minuteTensPosStr, minuteUnitPosStr, secondTensPosStr, secondUnitPosStr ->
            viewMap[POSITION_HOUR_TENS]!!.run {
                this[DigitPosition.TOP]!!.text = hourTensPosStr
                this[DigitPosition.BELOW]!!.text = hourTensPosStr
            }

            viewMap[POSITION_HOUR_UNIT]!!.run {
                this[DigitPosition.TOP]!!.text = hourUnitPosStr
                this[DigitPosition.BELOW]!!.text = hourUnitPosStr
            }

            viewMap[POSITION_MINUTE_TENS]!!.run {
                this[DigitPosition.TOP]!!.text = minuteTensPosStr
                this[DigitPosition.BELOW]!!.text = minuteTensPosStr
            }

            viewMap[POSITION_MINUTE_UNIT]!!.run {
                this[DigitPosition.TOP]!!.text = minuteUnitPosStr
                this[DigitPosition.BELOW]!!.text = minuteUnitPosStr
            }

            viewMap[POSITION_SECOND_TENS]!!.run {
                this[DigitPosition.TOP]!!.text = secondTensPosStr
                this[DigitPosition.BELOW]!!.text = secondTensPosStr
            }

            viewMap[POSITION_SECOND_UNIT]!!.run {
                this[DigitPosition.TOP]!!.text = secondUnitPosStr
                this[DigitPosition.BELOW]!!.text = secondUnitPosStr
            }
        }
        if (milliSec > 0) {
            startCountDownTimer(milliSec, onOverTime)
        }
    }

    /**
     * Tick 마다 설정
     * - 애니메이션 시작!!!
     */
    override fun onChangedTimes(
        onChangedTimeList: List<Pair<Int, Int>>,
        onNotChangedTimeList: List<Int>
    ) {
        onNotChangedTimeList.forEach { resetIndividualDigitView(it, prevTimeMap[it].toString()) }

        onChangedTimeList.forEach { changedPair ->
            viewMap[changedPair.first]?.run {
                this[DigitPosition.TOP]!!.text = changedPair.second.toString()
                this[DigitPosition.BELOW]!!.text = prevTimeMap[changedPair.first].toString()
            }
            startDigitAnim(changedPair.first, DigitPosition.TOP)
            startDigitAnim(changedPair.first, DigitPosition.BELOW)
        }
    }

    /**
     * 새로 생성하고 초기화 하는 과정에서 below 와 top 의 view에 이전 값들이 남게 되는데 이때
     * 각 숫자들이 깨지게 된다. 이를 방지하기 위해 하나 업데이트 할때 업데이트 되지 않는 Digit를 한번
     * 초기화 해준다.
     */
    private fun resetIndividualDigitView(viewType: Int, resetValue: String) {
        viewMap[viewType]!!.run {
            this[DigitPosition.TOP]!!.text = resetValue
            this[DigitPosition.BELOW]!!.text = resetValue
        }
    }
}