package co.icanteach.malibu.animator

import co.icanteach.malibu.SpeechRecognitionBar


class SpeechRecognitionIdleStateAnimator(private val floatingAmplitude: Int, private val bars: List<SpeechRecognitionBar>) : SpeechRecognitionAnimator {

    private val IDLE_DURATION: Long = 1500

    private var startTimestamp: Long = 0
    private var isPlaying: Boolean = false

    override fun start() {
        isPlaying = true
        startTimestamp = System.currentTimeMillis()
    }

    override fun stop() {
        isPlaying = false
    }

    override fun animate() {
        if (isPlaying) {
            update(bars)
        }
    }

    fun update(bars: List<SpeechRecognitionBar>) {

        val currTimestamp = System.currentTimeMillis()
        if (currTimestamp - startTimestamp > IDLE_DURATION) {
            startTimestamp += IDLE_DURATION
        }
        val delta = currTimestamp - startTimestamp

        var i = 0
        for (bar in bars) {
            updateCirclePosition(bar, delta, i)
            i++
        }
    }

    private fun updateCirclePosition(bar: SpeechRecognitionBar, delta: Long, num: Int) {
        val angle = delta.toFloat() / IDLE_DURATION * 360f + 120f * num
        val y = (Math.sin(Math.toRadians(angle.toDouble())) * floatingAmplitude).toInt() + bar.startY
        bar.y = y
        bar.update()
    }
}