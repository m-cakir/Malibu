package co.icanteach.malibu

import android.graphics.RectF;

data class SpeechRecognitionBar(val x: Int,
                                val y: Int,
                                val height: Int,
                                val maxHeight: Int,
                                val radius: Int,
                                val startX: Int = x,
                                val startY: Int = y,
                                val rect: RectF = RectF(
                                        (x - radius).toFloat(),
                                        (y - height / 2).toFloat(),
                                        (x + radius).toFloat(),
                                        (y + height / 2).toFloat())
) {


    fun update() {

        copy(rect = RectF(
                (x - radius).toFloat(),
                (y - height / 2).toFloat(),
                (x + radius).toFloat(),
                (y + height / 2).toFloat()))

    }
}