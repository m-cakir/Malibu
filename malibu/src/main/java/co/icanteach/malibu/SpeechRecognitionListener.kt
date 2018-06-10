package co.icanteach.malibu

import android.speech.RecognitionListener
import android.os.Bundle



abstract class SpeechRecognitionListener : RecognitionListener {

    override fun onReadyForSpeech(params: Bundle) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray) {}

    override fun onEndOfSpeech() {}

    override fun onResults(results: Bundle) {}

    override fun onPartialResults(partialResults: Bundle) {}

    override fun onEvent(eventType: Int, params: Bundle) {}
}