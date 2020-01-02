package com.suein1209.countdowntimer

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.suein1209.countdowntimer.common.WCountDownTimeAb

/**
 * Image Count Down Timer
 *
 * Created by suein1209
 */
class WCountDownTimerImageView : WCountDownTimeAb<ImageView> {
    //@formatter:off
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super( context, attrs, defStyleAttr )
    //@formatter:on

    /**
     * 숫자 이미지가 Resource Id가 저장되어 있는 Map
     */
    private lateinit var digitResMap: MutableMap<Int, Int>


    lateinit var colon1: ImageView
    lateinit var colon2: ImageView
    override fun initLayout(): View {
        digitResMap = mutableMapOf()

        val view = LayoutInflater.from(context).inflate(R.layout.view_imageview, this, true)
        colon1 = view.findViewById(R.id.colon1)
        colon2 = view.findViewById(R.id.colon2)
        return view
    }

    /**
     * XML 에 설정되어 있는 값을 세팅한다.
     */
    override fun afterInitLayout(attrs: AttributeSet?, defStyleAttr: Int?) {
        attrs?.let {
            val attr =
                context.obtainStyledAttributes(attrs, R.styleable.WCountDownTimerImageView, 0, 0)

            //XML에 설정되어 있는 이미지 Resource ID 를 설정한다.
            initImageResource(attr)

            /* [START] 지정된 이미지의 가로 세로 사이지를 설정한다. */
            val digitImgWidth = attr.getLayoutDimension(
                R.styleable.WCountDownTimerImageView_digitImgWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val digitImgHeight = attr.getDimensionPixelSize(
                R.styleable.WCountDownTimerImageView_digitImgHeight,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            initImageViewSize(digitImgWidth, digitImgHeight)
            /* [END] 지정된 이미지의 가로 세로 사이지를 설정한다. */

            //XML에 설정된 Animation Interpolator을 설정한다.
            interpolator = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImgAnim, 0)

            //콜론 이미지를 설정한다.(없으면 기본값으로 대체한다.)
            val colonRes = attr.getResourceId(
                R.styleable.WCountDownTimerImageView_digitcolon,
                R.drawable.default_colon
            )
            colon1.setImageResource(colonRes)
            colon2.setImageResource(colonRes)
            val digitColonImgWidth = attr.getLayoutDimension(
                R.styleable.WCountDownTimerImageView_digitColonImgWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val digitColonImgHeight = attr.getDimensionPixelSize(
                R.styleable.WCountDownTimerImageView_digitColonImgHeight,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            colon1.layoutParams = LayoutParams(digitColonImgWidth, digitColonImgHeight)
            colon2.layoutParams = LayoutParams(digitColonImgWidth, digitColonImgHeight)
        }
    }

    /**
     * XML에 설정되어 있는 이미지 Resource ID 를 설정한다.
     */
    private fun initImageResource(attr: TypedArray) {
        digitResMap[0] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg0, 0)
        digitResMap[1] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg1, 0)
        digitResMap[2] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg2, 0)
        digitResMap[3] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg3, 0)
        digitResMap[4] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg4, 0)
        digitResMap[5] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg5, 0)
        digitResMap[6] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg6, 0)
        digitResMap[7] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg7, 0)
        digitResMap[8] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg8, 0)
        digitResMap[9] = attr.getResourceId(R.styleable.WCountDownTimerImageView_digitImg9, 0)

        //꼭 10개를 모두 설정해야 한다.!!!
        require(digitResMap.values.find { it == 0 } == null) { "0~9 이미지가 모두 설정되어 있어야 합니다." }
    }

    /**
     * 이미지 리소스 사이즈를 설정한다.
     */
    private fun initImageViewSize(digitImgWidth: Int, digitImgHeight: Int) {
        positionTypeArray.forEach { positionType ->
            viewMap[positionType]!!.run {
                this[DigitPosition.TOP]!!.layoutParams =
                    RelativeLayout.LayoutParams(digitImgWidth, digitImgHeight)
                this[DigitPosition.BELOW]!!.layoutParams =
                    RelativeLayout.LayoutParams(digitImgWidth, digitImgHeight)
            }
        }
    }

    /**
     * DownCountTimer(with Animation)를 시작한다.
     * - 만약 설정되는 밀리초가 0 이라면 00:00:00으로 세팅만 하고 timer는 시작하지 않는다.
     */
    override fun start(milliSec: Long, onOverTime: (() -> Unit)?) {

        //init 설정
        convertTimerToSeveralDigit(milliSec) { hourTensPosDigit, hourUnitPosDigit, minuteTensPosDigit, minuteUnitPosDigit, secondTensPosDigit, secondUnitPosDigit ->
            viewMap[POSITION_HOUR_TENS]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[hourTensPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[hourTensPosDigit]!!)
            }

            viewMap[POSITION_HOUR_UNIT]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[hourUnitPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[hourUnitPosDigit]!!)
            }

            viewMap[POSITION_MINUTE_TENS]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[minuteTensPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[minuteTensPosDigit]!!)
            }

            viewMap[POSITION_MINUTE_UNIT]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[minuteUnitPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[minuteUnitPosDigit]!!)
            }

            viewMap[POSITION_SECOND_TENS]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[secondTensPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[secondTensPosDigit]!!)
            }

            viewMap[POSITION_SECOND_UNIT]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[secondUnitPosDigit]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[secondUnitPosDigit]!!)
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
        onChangedTimeList.forEach { changedPair ->

            viewMap[changedPair.first]!!.run {
                this[DigitPosition.TOP]!!.setImageResource(digitResMap[changedPair.second]!!)
                this[DigitPosition.BELOW]!!.setImageResource(digitResMap[prevTimeMap[changedPair.first]]!!)
            }
            startDigitAnim(changedPair.first, DigitPosition.TOP)
            startDigitAnim(changedPair.first, DigitPosition.BELOW)
        }
    }
}