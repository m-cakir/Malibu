package co.icanteach.malibu

import android.graphics.RectF;

data class SpeechRecognitionBar(var x: Int,
                                var y: Int,
                                var height: Int,
                                val maxHeight: Int,
                                val radius: Int,
                                val startX: Int = x,
                                val startY: Int = y,
                                var rect: RectF = RectF(
                                        (x - radius).toFloat(),
                                        (y - height / 2).toFloat(),
                                        (x + radius).toFloat(),
                                        (y + height / 2).toFloat())
) {


    fun update() {
        rect = RectF(
                (x - radius).toFloat(),
                (y - height / 2).toFloat(),
                (x + radius).toFloat(),
                (y + height / 2).toFloat())
    }
}