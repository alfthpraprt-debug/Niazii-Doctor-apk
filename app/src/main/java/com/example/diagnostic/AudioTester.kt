package com.example.diagnostic

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

class AudioTester {

    private var toneTrack: AudioTrack? = null
    private var isPlayingTone = false

    fun playDiagnosticTone(frequency: Double = 440.0, durationSeconds: Double = 2.0) {
        stopTone()
        isPlayingTone = true

        val sampleRate = 44100
        val numSamples = (durationSeconds * sampleRate).toInt()
        val generatedSnd = ByteArray(2 * numSamples)

        var idx = 0
        for (i in 0 until numSamples) {
            val dVal = sin(2.0 * Math.PI * i.toDouble() / (sampleRate / frequency))
            val valShort = (dVal * 32767).toInt().toShort()
            generatedSnd[idx++] = (valShort.toInt() and 0x00ff).toByte()
            generatedSnd[idx++] = ((valShort.toInt() and 0xff00) ushr 8).toByte()
        }

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(generatedSnd.size)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(generatedSnd, 0, generatedSnd.size)
        track.play()
        toneTrack = track
    }

    fun stopTone() {
        try {
            toneTrack?.stop()
            toneTrack?.release()
        } catch (_: Exception) {}
        toneTrack = null
        isPlayingTone = false
    }

    @SuppressLint("MissingPermission")
    suspend fun recordAndPlaybackSample(seconds: Int = 3): Boolean = withContext(Dispatchers.IO) {
        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (bufferSize <= 0) return@withContext false

        var recorder: AudioRecord? = null
        var player: AudioTrack? = null

        try {
            recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            val totalBytes = sampleRate * 2 * seconds
            val audioData = ByteArray(totalBytes)

            recorder.startRecording()
            var bytesRead = 0
            while (bytesRead < totalBytes) {
                val read = recorder.read(audioData, bytesRead, totalBytes - bytesRead)
                if (read <= 0) break
                bytesRead += read
            }
            recorder.stop()
            recorder.release()
            recorder = null

            // Playback recorded audio to confirm mic captured sound
            player = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(audioData.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            player.write(audioData, 0, bytesRead)
            player.play()

            // Wait for playback to finish
            kotlinx.coroutines.delay((seconds * 1000L) + 500L)
            player.stop()
            player.release()
            player = null
            true
        } catch (_: Exception) {
            try { recorder?.release() } catch (_: Exception) {}
            try { player?.release() } catch (_: Exception) {}
            false
        }
    }
}
