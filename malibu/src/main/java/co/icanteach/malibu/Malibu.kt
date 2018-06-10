package co.icanteach.malibu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.support.annotation.NonNull
import android.util.AttributeSet
import android.view.View
import co.icanteach.malibu.animator.*


class Malibu : View, RecognitionListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognitionListener: RecognitionListener

    private lateinit var paint: Paint

    private val recognitionBars = ArrayList<SpeechRecognitionBar>()
    private var animator: SpeechRecognitionAnimator? = null

    //flags.
    private var isSomeoneSpeaking = false
    private var isMalibuAnimationOnFire = false

    val BARS_COUNT = 5

    private val CIRCLE_RADIUS_DP = 5
    private val CIRCLE_SPACING_DP = 11
    private val ROTATION_RADIUS_DP = 25
    private val IDLE_FLOATING_AMPLITUDE_DP = 3

    private val DEFAULT_BARS_HEIGHT_DP = intArrayOf(60, 46, 70, 54, 64)

    private var radius: Int = 0
    private var spacing: Int = 0
    private var rotationRadius: Int = 0
    private var amplitude: Int = 0

    private var density: Int = 0

    private var barColor = -1
    private var barColors: IntArray? = null
    private var barMaxHeights: IntArray? = null

    // constructors.
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (recognitionBars.isEmpty()) {
            initBars();
        } else if (changed) {
            recognitionBars.clear();
            initBars();
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (recognitionBars.isEmpty()) {
            return
        }

        if (isMalibuAnimationOnFire) {
            animator?.animate()
        }

        for (i in 0 until recognitionBars.size) {
            val bar = recognitionBars[i]
            if (barColors != null) {
                paint.color = barColors!![i]
            } else if (barColor != -1) {
                paint.color = barColor
            }
            canvas?.drawRoundRect(bar.rect, radius.toFloat(), radius.toFloat(), paint)
        }

        if (isMalibuAnimationOnFire) {
            invalidate()
        }
    }

    // RecognitionListener.
    override fun onReadyForSpeech(params: Bundle?) {
        requireNotNull(recognitionListener)
        recognitionListener.onReadyForSpeech(params)
    }

    override fun onRmsChanged(rmsdB: Float) {
        requireNotNull(recognitionListener)

        recognitionListener.onRmsChanged(rmsdB)

        if (animator == null || rmsdB < 1f) {
            return
        }
        if (animator !is SpeechRecognitionMainRmsStateAnimator && isSomeoneSpeaking) {
            startRmsChangedStateAnimation()
        }
        if (animator is SpeechRecognitionMainRmsStateAnimator) {
            (animator as SpeechRecognitionMainRmsStateAnimator).onRmsChanged(rmsdB)
        }
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        requireNotNull(recognitionListener)
        recognitionListener.onBufferReceived(buffer)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        requireNotNull(recognitionListener)
        recognitionListener.onPartialResults(partialResults)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        requireNotNull(recognitionListener)
        recognitionListener.onEvent(eventType, params)
    }

    override fun onBeginningOfSpeech() {
        requireNotNull(recognitionListener)
        recognitionListener.onBeginningOfSpeech()
        isSomeoneSpeaking = true
    }

    override fun onEndOfSpeech() {
        requireNotNull(recognitionListener)
        startTransformInterpolation()
        isSomeoneSpeaking = false
    }

    override fun onError(error: Int) {
        requireNotNull(recognitionListener)
        recognitionListener.onError(error)
    }

    override fun onResults(results: Bundle?) {
        requireNotNull(recognitionListener)
        recognitionListener.onResults(results)
    }

    fun setSpeechRecognizer(@NonNull recognizer: SpeechRecognizer) {
        speechRecognizer = recognizer
        speechRecognizer.setRecognitionListener(this)
    }

    fun setRecognitionListener(@NonNull listener: RecognitionListener) {
        recognitionListener = listener
    }

    fun play() {
        startIdleStateAnimation()
        isMalibuAnimationOnFire = true
    }

    fun stop() {

        animator?.let { it ->
            it.stop()
            animator = null
        }
        isMalibuAnimationOnFire = false
        resetBars()
    }

    /**
     * Set one color to all bars in view
     */
    fun setSingleColor(color: Int) {
        barColor = color
    }

    /**
     * Set different colors to bars in view
     *
     * @param colors - array with size = [.BARS_COUNT]
     */
    fun setColors(colors: IntArray?) {
        if (colors == null) return

        barColors = IntArray(BARS_COUNT)
        if (colors.size < BARS_COUNT) {
            System.arraycopy(colors, 0, barColors, 0, colors.size)
            for (i in colors.size until BARS_COUNT) {
                barColors!![i] = colors[0]
            }
        } else {
            System.arraycopy(colors, 0, barColors, 0, BARS_COUNT)
        }
    }

    fun setBarMaxHeightsInDp(heights: IntArray?) {
        if (heights == null) return

        barMaxHeights = IntArray(BARS_COUNT)
        if (heights.size < BARS_COUNT) {
            System.arraycopy(heights, 0, barMaxHeights, 0, heights.size)
            for (i in heights.size until BARS_COUNT) {
                barMaxHeights!![i] = heights[0]
            }
        } else {
            System.arraycopy(heights, 0, barMaxHeights, 0, BARS_COUNT)
        }
    }

    /**
     * Set radius of circle
     *
     * @param radius - Default value = [.CIRCLE_RADIUS_DP]
     */
    fun setCircleRadiusInDp(radius: Int) {
        this.radius = (radius * density).toInt()
    }

    /**
     * Set spacing between circles
     *
     * @param spacing - Default value = [.CIRCLE_SPACING_DP]
     */
    fun setSpacingInDp(spacing: Int) {
        this.spacing = (spacing * density).toInt()
    }

    /**
     * Set idle animation amplitude
     *
     * @param amplitude - Default value = [.IDLE_FLOATING_AMPLITUDE_DP]
     */
    fun setIdleStateAmplitudeInDp(amplitude: Int) {
        this.amplitude = (amplitude * density) as Int
    }

    /**
     * Set rotation animation radius
     *
     * @param radius - Default value = [.ROTATION_RADIUS_DP]
     */
    fun setRotationRadiusInDp(radius: Int) {
        this.rotationRadius = (radius * density) as Int
    }

    private fun init(context: Context, attrs: AttributeSet? = null) {

        paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.color = Color.GREEN

        density = getResources().getDisplayMetrics().density.toInt();

        radius = ((CIRCLE_RADIUS_DP * density).toInt());
        spacing = ((CIRCLE_SPACING_DP * density).toInt())
        rotationRadius = ((ROTATION_RADIUS_DP * density).toInt())
        amplitude = ((IDLE_FLOATING_AMPLITUDE_DP * density).toInt())

        amplitude *= 2
    }

    private fun initBars() {
        val heights = initBarHeights()
        val firstCirclePosition = measuredWidth / 2 -
                2 * spacing -
                4 * radius
        for (i in 0 until BARS_COUNT) {
            val x = firstCirclePosition + (2 * radius + spacing) * i
            val bar = SpeechRecognitionBar(x, measuredHeight / 2, 2 * radius, heights.get(i), radius)
            recognitionBars.add(bar)
        }
    }

    private fun initBarHeights(): List<Int> {
        val barHeights = ArrayList<Int>()
        if (barMaxHeights == null) {
            for (i in 0 until BARS_COUNT) {
                barHeights.add((DEFAULT_BARS_HEIGHT_DP[i] * density) as Int)
            }
        } else {
            for (i in 0 until BARS_COUNT) {
                barHeights.add((barMaxHeights!![i] * density) as Int)
            }
        }
        return barHeights
    }


    private fun startIdleStateAnimation() {
        animator = SpeechRecognitionIdleStateAnimator(amplitude, recognitionBars)
        (animator as SpeechRecognitionIdleStateAnimator).start()
    }

    private fun startRmsChangedStateAnimation() {
        resetBars();
        animator = SpeechRecognitionMainRmsStateAnimator(recognitionBars)
        (animator as SpeechRecognitionMainRmsStateAnimator).start()
    }

    private fun startRotateInterpolation() {
        animator = SpeechRecognitionRotatingAnimator(recognitionBars, width / 2, height / 2)
        (animator as SpeechRecognitionRotatingAnimator).start()
    }

    private fun startTransformInterpolation() {

        resetBars()
        animator = SpeechRecognitionTransformAnimator(recognitionBars, width / 2, height / 2, rotationRadius)
        (animator as SpeechRecognitionTransformAnimator).start()

        (animator as SpeechRecognitionTransformAnimator)
                .setOnInterpolationFinishedListener(object : SpeechRecognitionTransformAnimator.OnInterpolationFinishedListener {
                    override fun onFinished() {
                        startRotateInterpolation()
                    }
                })
    }

    private fun resetBars() {
        for (bar in recognitionBars) {
            bar.run {
                copy(x = startX, y = startY, height = radius * 2)
                update()
            }
        }
    }

    companion object {
        val BARS_COUNT = 5
    }
}