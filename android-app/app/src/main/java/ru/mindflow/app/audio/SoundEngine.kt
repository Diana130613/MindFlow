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

    // Bird 1 chirp state machine
    private var birdTimer     = sr / 2
    private var birdActive    = false
    private var birdPhase     = 0.0
    private var birdFreqStart = 2500.0
    private var birdFreqEnd   = 3200.0
    private var birdChirpLen  = 1
    private var birdChirpPos  = 0

    // Bird 2 — independent rhythm, slightly lower register
    private var bird2Timer     = sr            // offset so they don't overlap at start
    private var bird2Active    = false
    private var bird2Phase     = 0.0
    private var bird2FreqStart = 1800.0
    private var bird2FreqEnd   = 2400.0
    private var bird2ChirpLen  = 1
    private var bird2ChirpPos  = 0

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

    // ── Birds: two independent birds with frequency sweep + envelope ─────────
    private fun fillBirds(buf: FloatArray) {
        for (i in buf.indices) {
            // Forest background: two layered lowpasses for richer texture
            lpA = 0.96f * lpA + 0.04f * w()
            lpB = 0.99f * lpB + 0.01f * w()
            val bg = lpA * 0.06f + lpB * 0.03f

            // Bird 1 state machine
            if (--birdTimer <= 0) {
                birdActive = !birdActive
                if (birdActive) {
                    birdFreqStart = 2000.0 + Random.nextDouble() * 1500.0
                    // sweep up or mostly down — natural bird calls tend to fall
                    birdFreqEnd   = birdFreqStart + (Random.nextDouble() - 0.35) * 900.0
                    birdChirpLen  = (sr * (0.05 + Random.nextDouble() * 0.12)).toInt().coerceAtLeast(1)
                    birdChirpPos  = 0
                    birdPhase     = 0.0
                    birdTimer     = birdChirpLen
                } else {
                    birdTimer = (sr * (0.6 + Random.nextDouble() * 2.2)).toInt().coerceAtLeast(1)
                }
            }

            val chirp1 = if (birdActive) {
                val t    = birdChirpPos.toDouble() / birdChirpLen
                birdChirpPos++
                val freq = birdFreqStart + (birdFreqEnd - birdFreqStart) * t
                birdPhase += TAU * freq / sr
                // sin envelope: rises then falls smoothly
                val env  = sin(t * PI).toFloat()
                sin(birdPhase).toFloat() * env * 0.22f
            } else 0f

            // Bird 2 state machine
            if (--bird2Timer <= 0) {
                bird2Active = !bird2Active
                if (bird2Active) {
                    bird2FreqStart = 1500.0 + Random.nextDouble() * 1200.0
                    bird2FreqEnd   = bird2FreqStart + (Random.nextDouble() - 0.4) * 700.0
                    bird2ChirpLen  = (sr * (0.04 + Random.nextDouble() * 0.09)).toInt().coerceAtLeast(1)
                    bird2ChirpPos  = 0
                    bird2Phase     = 0.0
                    bird2Timer     = bird2ChirpLen
                } else {
                    bird2Timer = (sr * (1.2 + Random.nextDouble() * 3.5)).toInt().coerceAtLeast(1)
                }
            }

            val chirp2 = if (bird2Active) {
                val t    = bird2ChirpPos.toDouble() / bird2ChirpLen
                bird2ChirpPos++
                val freq = bird2FreqStart + (bird2FreqEnd - bird2FreqStart) * t
                bird2Phase += TAU * freq / sr
                val env  = sin(t * PI).toFloat()
                sin(bird2Phase).toFloat() * env * 0.16f
            } else 0f

            buf[i] = bg + chirp1 + chirp2
        }
    }

    companion object {
        private const val TAU = 2.0 * PI
    }
}