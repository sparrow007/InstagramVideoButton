package com.jackandphantom.instagramvideobutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.animation.ValueAnimator
import android.graphics.RectF
import android.view.GestureDetector
import android.view.animation.*
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.AnimatorListenerAdapter

class InstagramVideoButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    interface ActionListener {
        fun onStartRecord()

        fun onEndRecord()

        fun onSingleTap()

        fun onDurationTooShortError()

        fun onCancelled()
    }

    var actionListener: ActionListener? = null

    private var progress: Float = 0f
    private val maxProgress: Float = 360f
    private val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val outerArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var desiredWidth : Int
    private var desiredHeight : Int

    private var outerCircleRadius : Float
    private var innerCircleRadius : Float
    private var outerCircleAnimatingFactor : Float
    private var innerCircleAnimatingFactor : Float

    private var STROKE_WIDTH : Float = 13f

    private var initialInnerCircleRadius : Float = 0f
    private var initialOuterCircleRadius : Float = 0f

    private var diffInnerCircleRaius : Float = 0f
    private var diffOuterCircleRaius : Float = 0f

    private var sweeAngle:Float = 0f

    private var arcRect : RectF

    private var isVideoStart: Boolean = false
    private var startTimeInMills : Long = 0
    private var endTimeInMills : Long = 0

    private var isLongPressEndCalled : Boolean = false
    private var enablePhotoTaking : Boolean = false
    private var enableVideoRecording: Boolean = false

    private var minimumVideoDuration : Long = 0L
    private var videoDuration : Long = 0L

    private val DEFAULT_MINIMUM_RECORDING_TIME = 500L
    private val DEFAULT_VIDEO_RECORDING_TIME= 10000L
    private val START_ANGLE = 270f


    private var divisionFactor : Float = 0f
    /*
    * Initializing and getting the value from xml attributes defing for the custom view
    * */
    init {
        val typedArray = context.theme.obtainStyledAttributes(attrs,R.styleable.InstagramVideoButton, defStyleAttr, defStyleAttr)
        outerCirclePaint.color = typedArray.getColor(R.styleable.InstagramVideoButton_outerCircleColor, Color.WHITE)
        innerCirclePaint.color = typedArray.getColor(R.styleable.InstagramVideoButton_innerCircleColor, Color.WHITE)
        outerArcPaint.color = typedArray.getColor(R.styleable.InstagramVideoButton_progressColor, Color.GRAY)
        outerCirclePaint.strokeWidth = typedArray.getFloat(R.styleable.InstagramVideoButton_outerCircleWidth, STROKE_WIDTH)
        enableVideoRecording = typedArray.getBoolean(R.styleable.InstagramVideoButton_enableVideoRecording, false)
        enablePhotoTaking = typedArray.getBoolean(R.styleable.InstagramVideoButton_enablePhotoTaking, false)

        outerCirclePaint.style = Paint.Style.STROKE
        outerArcPaint.style = Paint.Style.STROKE
        outerArcPaint.strokeWidth = STROKE_WIDTH
        outerArcPaint.strokeCap = Paint.Cap.ROUND
        desiredWidth = 200
        desiredHeight = 200
        outerCircleRadius = 0f

        outerCircleAnimatingFactor = 0f
        innerCircleRadius = 0f
        outerCircleAnimatingFactor = 20f
        innerCircleAnimatingFactor = 20f
        arcRect = RectF()
        minimumVideoDuration = DEFAULT_MINIMUM_RECORDING_TIME
        videoDuration = DEFAULT_VIDEO_RECORDING_TIME
    }

    private val progressAnimator = ObjectAnimator.ofFloat(this, "progress", 360f).apply {
        duration = videoDuration
        interpolator = LinearInterpolator()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if(isVideoStart)
                    longPressEnd()
            }
        })
    }

    private var singleTapValueAnimator = ValueAnimator.ofFloat().apply {
        interpolator = AccelerateInterpolator()
        duration = 300L
        addUpdateListener {
            innerCircleRadius = it.animatedValue as Float
            invalidate()
        }
    }

    private val LongPressEndAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
        addUpdateListener { animation -> run {
            val value = animation.animatedValue as Int

            outerCircleRadius -= value * (diffOuterCircleRaius/100)

            if(innerCircleRadius <= initialInnerCircleRadius)
                innerCircleRadius -= value * divisionFactor

            if(outerCircleRadius >= initialOuterCircleRadius )
                invalidate()
            else {
                outerCircleRadius = initialOuterCircleRadius
                innerCircleRadius = initialInnerCircleRadius
                invalidate()
                return@addUpdateListener
            }
        }
        }
    }

    private val LongPressStartAnimator = ValueAnimator.ofInt(0, 100).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
        addUpdateListener { animation -> run {
            val value = animation.animatedValue as Int
            outerCircleRadius += value * (diffOuterCircleRaius/100)
            if(innerCircleRadius >= (width/5f))
                innerCircleRadius += value * (diffInnerCircleRaius/100)
            if(outerCircleRadius <= width/2f )
                invalidate()

            else {
                outerCircleRadius = width / 2f
                invalidate()
                return@addUpdateListener
            }

        }
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if(!isLongPressEndCalled)
                    isVideoStart = true
                progressAnimator.start()
            }
        })
    }

    override fun onDraw(canvas: Canvas?) {

        if(canvas == null)
            return
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), innerCircleRadius, innerCirclePaint)
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), outerCircleRadius-STROKE_WIDTH/2, outerCirclePaint)

        if(isVideoStart)
            canvas.drawArc(arcRect, START_ANGLE, sweeAngle, false, outerArcPaint)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initialOuterCircleRadius = w/4f+STROKE_WIDTH
        outerCircleRadius = initialOuterCircleRadius

        initialInnerCircleRadius = w/4f-STROKE_WIDTH
        innerCircleRadius = initialInnerCircleRadius
        singleTapValueAnimator.setFloatValues(initialInnerCircleRadius, w/6f, initialInnerCircleRadius)
        val top = STROKE_WIDTH / 2
        val left = top
        val right = w - STROKE_WIDTH/2
        val bottom = right
        arcRect.set(left,top,right,bottom)

        diffOuterCircleRaius = ((width/2f) - outerCircleRadius)
        diffInnerCircleRaius = ((width/5f-20) - innerCircleRadius)
        divisionFactor = (diffInnerCircleRaius/100)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desireW = desiredWidth + paddingLeft + paddingRight
        val desireH = desiredHeight + paddingTop + paddingBottom

        setMeasuredDimension(measureDimension(desireW, widthMeasureSpec), measureDimension(desireH, heightMeasureSpec))
    }

    private fun measureDimension(desireSize : Int , measureSpec : Int) : Int {
        var result : Int
        val mode  = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        if(mode == MeasureSpec.EXACTLY) {
            result = size
        }else {
            result = desireSize

            if(mode == MeasureSpec.AT_MOST) {
                result = Math.min(size , result)
            }
        }
        return result
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val detectUp = event!!.action == MotionEvent.ACTION_UP
        if(!gestureDetector.onTouchEvent(event) && detectUp && enableVideoRecording)
            longPressEnd()
        return true
    }

    private val gestureDetector = GestureDetector(context, object :
        GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent?) {
            if(enableVideoRecording)
                onLongPressStart()

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if(enablePhotoTaking) {
                onSingleTap()
                return true
            }
            return super.onSingleTapUp(e)
        }
    })

    private fun onSingleTap() {
        actionListener?.onSingleTap()
        val tapAnimator = ValueAnimator.ofFloat(initialInnerCircleRadius, width/7f, initialInnerCircleRadius)
        tapAnimator.setDuration(300L)
        tapAnimator.interpolator = AccelerateInterpolator()
        tapAnimator.addUpdateListener { animator -> run{
            innerCircleRadius = animator.animatedValue as Float
            invalidate()
        } }
        tapAnimator.start()
    }

    private fun onLongPressStart() {
        isVideoStart = false
        isLongPressEndCalled = false
        startTimeInMills = System.currentTimeMillis()
        actionListener?.onStartRecord()
        LongPressStartAnimator.start()
    }

    fun cancelRecording() {
        isVideoStart = false
        isLongPressEndCalled = true
        sweeAngle = 0f
        endTimeInMills = System.currentTimeMillis()
        if(LongPressStartAnimator.isRunning())
            LongPressStartAnimator.end()
        progressAnimator.end()
        LongPressEndAnimator.start()
        actionListener?.onCancelled()
    }

    private fun setProgress(progress: Float) {
        if(progress<=maxProgress)
            this.progress = progress
        else
            this.progress = maxProgress
        sweeAngle =  (360 * this.progress / maxProgress)
        invalidate()
    }

    private fun longPressEnd() {
        isVideoStart = false
        isLongPressEndCalled = true
        sweeAngle = 0f
        endTimeInMills = System.currentTimeMillis()
        if(LongPressStartAnimator.isRunning())
            LongPressStartAnimator.end()
        progressAnimator.end()
        LongPressEndAnimator.start()
        if(isRecordingTooSmall(startTimeInMills, endTimeInMills, minimumVideoDuration)){
            actionListener?.onDurationTooShortError()
        }else
            actionListener?.onEndRecord()

        resetTimeFields()

    }

    private  fun isRecordingTooSmall(start: Long, end: Long,defaultTime: Long) : Boolean{
        return defaultTime > end - start
    }

    private fun resetTimeFields() {
        startTimeInMills = 0
        endTimeInMills = 0
    }

    fun setMinimumVideoDuration(recordingTime : Long) {
        minimumVideoDuration = DEFAULT_MINIMUM_RECORDING_TIME + recordingTime
    }

    fun setVideoDuration(recordingTime: Long) {
        videoDuration = recordingTime
    }

    fun setProgressColor(color : Int) {
        outerArcPaint.color = color
        invalidate()
    }

    fun setInnerCircleColor(color: Int) {
        innerCirclePaint.color = color
        invalidate()
    }

    fun setOuterCircleColor(color: Int) {
        outerCirclePaint.color = color
        invalidate()
    }

    fun enableVideoRecording(enableVideoRecording : Boolean) {
        this.enableVideoRecording = enableVideoRecording
    }

    fun enablePhotoTaking(enablePhotoTaking : Boolean) {
        this.enablePhotoTaking = enablePhotoTaking
    }

    fun setOuterCircleWidth(width : Float) {
        outerCirclePaint.strokeWidth = width
        invalidate()
    }

}