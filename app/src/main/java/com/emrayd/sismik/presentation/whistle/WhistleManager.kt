package com.emrayd.sismik.presentation.whistle

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class WhistleManager {

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val FREQUENCY_HZ = 2500.0
    }

    @Volatile
    private var isPlaying = false

    private var playbackThread: Thread? = null
    private var audioTrack: AudioTrack? = null

    fun start() {
        if (isPlaying) return
        isPlaying = true

        playbackThread = Thread {
            val minBufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            val buffer = ShortArray(minBufferSize / 2)
            var phase = 0.0
            val phaseIncrement = 2 * PI * FREQUENCY_HZ / SAMPLE_RATE

            while (isPlaying) {
                for (i in buffer.indices) {
                    buffer[i] = (sin(phase) * Short.MAX_VALUE).toInt().toShort()
                    phase += phaseIncrement
                    if (phase > 2 * PI) phase -= 2 * PI
                }
                track.write(buffer, 0, buffer.size)
            }

            track.stop()
            track.release()
        }
        playbackThread?.start()
    }

    fun stop() {
        isPlaying = false
        playbackThread?.join(200)
        playbackThread = null
        audioTrack = null
    }
}