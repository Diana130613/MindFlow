package ru.mindflow.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.*
import kotlin.random.Random

/**
 * Procedural ambient sound generator using Android AudioTrack.
 * All sounds are synthesized in real-time — no audio files needed.
 */
class SoundEngine {

    companion object {
        private const val SAMPLE_RATE  = 22050
        private const val BUFFER_FRAMES = 1024
    }

    @Volatile private var running = false
    private var audioTrack: AudioTrack? = null
    private var thread: Thread? = null

    enum class SoundType {
        FAN, STATIC, SHOWER, COFFEE,
        RAIN, SEA, FOREST, BIRDS
    }

    fun play(type: SoundType) {
        stop()
        running = true

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(BUFFER_FRAMES * 4 * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        thread = Thread {
            val buffer = FloatArray(BUFFER_FRAMES)
            val gen    = NoiseGenerator(type, SAMPLE_RATE)
            while (running && track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                gen.fill(buffer)
                val result = track.write(buffer, 0, buffer.size, AudioTrack.WRITE_BLOCKING)
                if (result < 0) break
            }
        }.also {
            it.isDaemon = true
            it.name = "SoundEngine-$type"
            it.start()
        }
    }

    fun stop() {
        running = false
        try { thread?.join(400) } catch (_: InterruptedException) {}
        try { audioTrack?.stop() } catch (_: Exception) {}
        audioTrack?.release()
        audioTrack = null
        thread = null
    }

    fun isPlaying() = running
}

// ── Noise generator algorithms ────────────────────────────────────────────────

private class NoiseGenerator(private val type: SoundEngine.SoundType, private val sr: Int) {

    // IIR filter state
    private var lpA = 0f
    private var lpB = 0f

    // Phase / timing accumulators
    private var globalPhase = 0.0
    private var wavePhase   = 0.0

    // Bird chirp state machine
    private var birdTimer  = sr / 2       // samples until next state flip
    private var birdActive = false
    private var birdPhase  = 0.0
    private var birdFreq   = 2500.0

    fun fill(buf: FloatArray) = when (type) {
        SoundEngine.SoundType.STATIC  -> fillWhiteNoise(buf)
        SoundEngine.SoundType.FAN     -> fillFan(buf)
        SoundEngine.SoundType.SHOWER  -> fillShower(buf)
        SoundEngine.SoundType.COFFEE  -> fillCoffee(buf)
        SoundEngine.SoundType.RAIN    -> fillRain(buf)
        SoundEngine.SoundType.SEA     -> fillSea(buf)
        SoundEngine.SoundType.FOREST  -> fillForest(buf)
        SoundEngine.SoundType.BIRDS   -> fillBirds(buf)
    }

    private fun w() = Random.nextFloat() * 2f - 1f  // white noise sample

    // ── White noise ──────────────────────────────────────────────────────────
    private fun fillWhiteNoise(buf: FloatArray) {
        for (i in buf.indices) buf[i] = w() * 0.35f
    }

    // ── Fan: heavy lowpass → deep, smooth hum ────────────────────────────────
    private fun fillFan(buf: FloatArray) {
        for (i in buf.indices) {
            lpA = 0.97f * lpA + 0.03f * w()
            buf[i] = lpA * 0.75f
        }
    }

    // ── Shower: medium lowpass + raw brightness → splashy noise ─────────────
    private fun fillShower(buf: FloatArray) {
        for (i in buf.indices) {
            val n = w()
            lpA = 0.78f * lpA + 0.22f * n
            buf[i] = (lpA * 0.40f + n * 0.15f)
        }
    }

    // ── Rain: two overlapping lowpasses for pink-ish character ───────────────
    private fun fillRain(buf: FloatArray) {
        for (i in buf.indices) {
            val n = w()
            lpA = 0.93f * lpA + 0.07f * n
            lpB = 0.98f * lpB + 0.02f * n
            buf[i] = (lpA * 0.40f + lpB * 0.30f + n * 0.08f) * 0.65f
        }
    }

    // ── Sea: very slow amplitude-modulated lowpass (wave rhythm ≈ 6 s) ───────
    private fun fillSea(buf: FloatArray) {
        for (i in buf.indices) {
            wavePhase += TAU / (sr * 6.0)
            val wave = (0.25 + 0.75 * sin(wavePhase)).toFloat().coerceAtLeast(0f)
            lpA = 0.99f * lpA + 0.01f * w()
            buf[i] = lpA * wave * 0.9f
        }
    }

    // ── Coffee machine: periodic gurgle ~4 Hz ────────────────────────────────
    private fun fillCoffee(buf: FloatArray) {
        for (i in buf.indices) {
            globalPhase += TAU * 4.0 / sr
            val mod = (0.35 + 0.65 * abs(sin(globalPhase))).toFloat()
            lpA = 0.88f * lpA + 0.12f * w()
            buf[i] = lpA * mod * 0.65f
        }
    }

    // ── Forest: medium-freq modulated wind-through-leaves ────────────────────
    private fun fillForest(buf: FloatArray) {
        for (i in buf.indices) {
            wavePhase += TAU / (sr * 3.5)
            val mod = (0.55 + 0.45 * sin(wavePhase)).toFloat()
            lpA = 0.94f * lpA + 0.06f * w()
            buf[i] = lpA * mod * 0.55f
        }
    }

    // ── Birds: chirp tones (2–4 kHz) over soft forest background ─────────────
    private fun fillBirds(buf: FloatArray) {
        for (i in buf.indices) {
            // Soft background (forest)
            lpA = 0.96f * lpA + 0.04f * w()
            val bg = lpA * 0.09f

            // Chirp state machine
            if (--birdTimer <= 0) {
                birdActive = !birdActive
                birdTimer = if (birdActive) {
                    birdFreq  = 2000.0 + Random.nextDouble() * 2000.0
                    birdPhase = 0.0
                    (sr * (0.04 + Random.nextDouble() * 0.10)).toInt().coerceAtLeast(1)
                } else {
                    (sr * (0.5 + Random.nextDouble() * 1.8)).toInt().coerceAtLeast(1)
                }
            }

            val chirp = if (birdActive) {
                birdPhase += TAU * birdFreq / sr
                sin(birdPhase).toFloat() * 0.22f
            } else 0f

            buf[i] = bg + chirp
        }
    }

    companion object {
        private const val TAU = 2.0 * PI
    }
}