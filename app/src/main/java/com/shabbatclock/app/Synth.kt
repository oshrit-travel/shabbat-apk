package com.shabbatclock.app

import kotlin.math.PI
import kotlin.math.sin

/** Built-in sounds, identical in character to the web version: flat 640Hz siren and a ding-dong chime. */
object Synth {
    const val RATE = 44100

    fun siren(seconds: Int): ShortArray {
        val n = seconds * RATE
        val fade = (0.25 * RATE).toInt()
        val out = ShortArray(n)
        for (i in 0 until n) {
            val env = when {
                i < fade -> i / fade.toDouble()
                i > n - fade -> (n - i) / fade.toDouble()
                else -> 1.0
            }
            out[i] = (sin(2 * PI * 640.0 * i / RATE) * 0.32 * env * 32767).toInt().toShort()
        }
        return out
    }

    /** Soft harp-like arpeggio: 4 rising plucked notes (C5 E5 G5 C6) that ring out, about 3 seconds. */
    fun chime(): ShortArray {
        val total = (3.2 * RATE).toInt()
        val out = ShortArray(total)
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.5)
        for ((n, freq) in notes.withIndex()) {
            val start = ((n * 0.3) * RATE).toInt()
            val len = (1.9 * RATE).toInt()
            val up = (0.012 * RATE).toInt()
            for (k in 0 until len) {
                val idx = start + k
                if (idx >= total) break
                val env = if (k < up) k / up.toDouble() else Math.exp(-k.toDouble() / RATE * 3.6)
                val tt = k.toDouble() / RATE
                val v = (sin(2 * PI * freq * tt) * 0.22 + sin(2 * PI * freq * 2 * tt) * 0.06) * env
                out[idx] = (out[idx] + (v * 32767).toInt()).coerceIn(-32768, 32767).toShort()
            }
        }
        return out
    }
}
