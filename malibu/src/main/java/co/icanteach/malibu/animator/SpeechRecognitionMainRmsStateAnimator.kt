package co.icanteach.malibu.animator

import co.icanteach.malibu.SpeechRecognitionBar


class SpeechRecognitionMainRmsStateAnimator(val recognitionBars: List<SpeechRecognitionBar>) : SpeechRecognitionAnimator {

    private var barAnimators = arrayListOf<SpeechRecognitionRmsStateAnimator>()

    init {
        for (bar in recognitionBars) {
            barAnimators.add(SpeechRecognitionRmsStateAnimator(bar))
        }
    }

    override fun start() {
        for (barAnimator in barAnimators) {
            barAnimator.start()
        }
    }

    override fun stop() {
        for (barAnimator in barAnimators) {
            barAnimator.stop()
        }
    }

    override fun animate() {
        for (barAnimator in barAnimators) {
            barAnimator.animate()
        }
    }

    fun onRmsChanged(rmsDB: Float) {
        for (barAnimator in barAnimators) {
            barAnimator.onRmsChanged(rmsDB)
        }
    }
}