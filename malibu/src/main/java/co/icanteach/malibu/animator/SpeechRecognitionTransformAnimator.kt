package co.icanteach.malibu.animator

import android.graphics.Point
import co.icanteach.malibu.Malibu
import co.icanteach.malibu.SpeechRecognitionBar


class SpeechRecognitionTransformAnimator(val bars: List<SpeechRecognitionBar>,
                                         val centerX: Int,
                                         val centerY: Int,
                                         val radius: Int) : SpeechRecognitionAnimator {

    private val DURATION: Long = 300

    private var startTimestamp: Long = 0
    private var isPlaying: Boolean = false

    private var listener: OnInterpolationFinishedListener? = null

    private var finalPositions: ArrayList<Point> = ArrayList()

    override fun start() {
        isPlaying = true
        startTimestamp = System.currentTimeMillis()
        initFinalPositions()
    }

    override fun stop() {
        isPlaying = false
        if (listener != null) {
            listener!!.onFinished()
        }
    }

    override fun animate() {
        if (!isPlaying) return

        val currTimestamp = System.currentTimeMillis()
        var delta = currTimestamp - startTimestamp
        if (delta > DURATION) {
            delta = DURATION
        }

        for (i in bars.indices) {
            val bar = bars[i]

            val x = bar.startX + ((finalPositions.get(i).x - bar.startY) * (delta.toFloat() / DURATION)).toInt()
            val y = bar.startY + ((finalPositions.get(i).y - bar.startY) * (delta.toFloat() / DURATION)).toInt()

            bar.x = x
            bar.y = y
            bar.update()
        }

        if (delta == DURATION) {
            stop()
        }
    }

    private fun initFinalPositions() {
        val startPoint = Point()
        startPoint.x = centerX
        startPoint.y = centerY - radius
        for (i in 0 until Malibu.BARS_COUNT) {
            val point = Point(startPoint)
            rotate(360.0 / Malibu.BARS_COUNT * i, point)
            finalPositions.add(point)
        }
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private fun rotate(degrees: Double, point: Point) {

        val angle = Math.toRadians(degrees)

        val x = centerX + ((point.x - centerX) * Math.cos(angle) - (point.y - centerY) * Math.sin(angle)).toInt()

        val y = centerY + ((point.x - centerX) * Math.sin(angle) + (point.y - centerY) * Math.cos(angle)).toInt()

        point.x = x
        point.y = y
    }

    fun setOnInterpolationFinishedListener(listener: OnInterpolationFinishedListener) {
        this.listener = listener
    }

    interface OnInterpolationFinishedListener {
        fun onFinished()
    }
}