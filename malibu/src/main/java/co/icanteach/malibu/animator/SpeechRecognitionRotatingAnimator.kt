package co.icanteach.malibu.animator

import android.graphics.Point
import android.view.animation.AccelerateDecelerateInterpolator
import co.icanteach.malibu.SpeechRecognitionBar


class SpeechRecognitionRotatingAnimator(val bars: List<SpeechRecognitionBar>,
                                        val centerX: Int,
                                        val centerY: Int) : SpeechRecognitionAnimator {

    private val DURATION: Long = 2000
    private val ACCELERATE_ROTATION_DURATION: Long = 1000
    private val DECELERATE_ROTATION_DURATION: Long = 1000
    private val ROTATION_DEGREES = 720f
    private val ACCELERATION_ROTATION_DEGREES = 40f

    private var startTimestamp: Long = 0
    private var isPlaying: Boolean = false

    private var startPositions = arrayListOf<Point>()

    init {
        for (bar in bars) {
            startPositions.add(Point(bar.x, bar.y))
        }
    }

    override fun start() {
        isPlaying = true
        startTimestamp = System.currentTimeMillis()
    }

    override fun stop() {
        isPlaying = false
    }

    override fun animate() {

        if (!isPlaying) return

        val currTimestamp = System.currentTimeMillis()
        if (currTimestamp - startTimestamp > DURATION) {
            startTimestamp += DURATION
        }

        val delta = currTimestamp - startTimestamp

        val interpolatedTime = delta.toFloat() / DURATION

        val angle = interpolatedTime * ROTATION_DEGREES

        for ((i, bar) in bars.withIndex()) {
            var finalAngle = angle
            if (i > 0 && delta > ACCELERATE_ROTATION_DURATION) {
                finalAngle += decelerate(delta, bars.size - i)
            } else if (i > 0) {
                finalAngle += accelerate(delta, bars.size - i)
            }
            rotate(bar, finalAngle.toDouble(), startPositions[i])
        }
    }

    private fun decelerate(delta: Long, scale: Int): Float {
        val accelerationDelta = delta - ACCELERATE_ROTATION_DURATION
        val interpolator = AccelerateDecelerateInterpolator()
        val interpolatedTime = interpolator.getInterpolation(accelerationDelta.toFloat() / DECELERATE_ROTATION_DURATION)
        val decelerationAngle = -interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale)
        return ACCELERATION_ROTATION_DEGREES * scale + decelerationAngle
    }

    private fun accelerate(delta: Long, scale: Int): Float {
        val interpolator = AccelerateDecelerateInterpolator()
        val interpolatedTime = interpolator.getInterpolation(delta.toFloat() / ACCELERATE_ROTATION_DURATION)
        return interpolatedTime * (ACCELERATION_ROTATION_DEGREES * scale)
    }

    /**
     * X = x0 + (x - x0) * cos(a) - (y - y0) * sin(a);
     * Y = y0 + (y - y0) * cos(a) + (x - x0) * sin(a);
     */
    private fun rotate(bar: SpeechRecognitionBar, degrees: Double, startPosition: Point) {

        val angle = Math.toRadians(degrees)

        val x = centerX + ((startPosition.x - centerX) * Math.cos(angle) - (startPosition.y - centerY) * Math.sin(angle)).toInt()

        val y = centerY + ((startPosition.x - centerX) * Math.sin(angle) + (startPosition.y - centerY) * Math.cos(angle)).toInt()

        bar.x = x
        bar.y = y
        bar.update()
    }

}