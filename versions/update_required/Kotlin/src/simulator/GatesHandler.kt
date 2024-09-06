package simulator

import model.ComplexNumber
import model.Gate
import util.MathConstants
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class GatesHandler {

    fun handleX(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        val target = gate.targetQubit
        val firstPow = 2.0.pow(target).toInt()
        val firstPlusPow = 2.0.pow(target + 1).toInt()
        val opposingPow = 2.0.pow(numberOfQubits - target - 1).toInt()

        val localAmplitudes = arrayListOf<ComplexNumber>()
        amplitudes.forEach { localAmplitudes.add(it.copy()) }

        for (i in 0 until firstPow) {
            var posJ = 0
            for (j in 0 until opposingPow) {
                val pos1 = i + posJ
                val pos2 = pos1 + firstPow

                val old = localAmplitudes[pos1]
                localAmplitudes[pos1] = localAmplitudes[pos2]
                localAmplitudes[pos2] = old

                posJ += firstPlusPow
            }
        }

        return localAmplitudes
    }

    fun handleRX(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        val target = gate.targetQubit
        val firstPow = 2.0.pow(target).toInt()
        val firstPlusPow = 2.0.pow(target + 1).toInt()
        val opposingPow = 2.0.pow(numberOfQubits - target - 1).toInt()

        val localAmplitudes = arrayListOf<ComplexNumber>()
        amplitudes.forEach { localAmplitudes.add(it.copy()) }

        gate.theta?.let { theta ->
            val cosTheta = cos(theta / 2)
            val sinTheta = sin(theta / 2)

            for (i in 0 until firstPow) {
                var posJ = 0
                for (j in 0 until opposingPow) {
                    val pos1 = i + posJ
                    val pos2 = pos1 + firstPow

                    val p1 = localAmplitudes[pos1]
                    val p2 = localAmplitudes[pos2]

                    localAmplitudes[pos1].real = p1.real * cosTheta + p2.complex * sinTheta
                    localAmplitudes[pos1].complex = p1.complex * cosTheta - p2.real * sinTheta
                    localAmplitudes[pos2].real = p2.real * cosTheta + p1.complex * sinTheta
                    localAmplitudes[pos2].complex = p2.complex * cosTheta - p1.real * sinTheta

                    posJ += firstPlusPow
                }
            }

            return localAmplitudes
        }
        return emptyList()
    }

    fun handleCX(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        val target = gate.targetQubit
        val control = gate.controlQubit ?: 0

        var pow1: Int
        val pow2: Int
        val pow2Plus: Int
        var end2: Int
        var end3: Int

        val localAmplitudes = arrayListOf<ComplexNumber>()
        amplitudes.forEach { localAmplitudes.add(it.copy()) }

        val firstPow: Int = 2.0.pow(target).toInt()
        val secondPow: Int = 2.0.pow(control).toInt()

        if (control < target) {
            pow1 = secondPow
            pow2Plus = firstPow * 2
            pow2 = firstPow
        } else {
            pow1 = firstPow
            pow2Plus = secondPow * 2
            pow2 = secondPow
        }

        val pow1Plus: Int = pow1 * 2
        val pow3: Int = 2.0.pow(numberOfQubits).toInt()

        pow1 += firstPow

        for (posI in firstPow until pow1) {
            end2 = pow2 + posI
            for (posJ in posI until end2 step pow1Plus) {
                end3 = pow3 + posJ
                for (posK in posJ until end3 step pow2Plus) {
                    val pos2 = posK + secondPow
                    val old = localAmplitudes[posK]
                    localAmplitudes[posK] = localAmplitudes[pos2]
                    localAmplitudes[pos2] = old
                }
            }
        }

        return localAmplitudes
    }

    fun handleCRX(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        val target = gate.targetQubit
        val control = gate.controlQubit ?: 0

        gate.theta?.let { theta ->
            val cosTheta = cos(theta / 2)
            val sinTheta = sin(theta / 2)
            var pow1: Int
            val pow2: Int
            val pow2Plus: Int
            var end2: Int

            val localAmplitudes = arrayListOf<ComplexNumber>()
            amplitudes.forEach { localAmplitudes.add(it.copy()) }

            val firstPow: Int = 2.0.pow(target).toInt()
            val secondPow: Int = 2.0.pow(control).toInt()

            if (control < target) {
                pow1 = secondPow
                pow2Plus = firstPow * 2
                pow2 = firstPow
            } else {
                pow1 = firstPow
                pow2Plus = secondPow * 2
                pow2 = secondPow
            }

            val pow1Plus: Int = pow1 * 2
            val pow3: Int = 2.0.pow(numberOfQubits).toInt()

            pow1 += firstPow

            for (posI in firstPow until pow1) {
                end2 = pow2 + posI
                for (posJ in posI until end2 step pow1Plus) {
                    val end3 = pow3 + posJ
                    for (posK in posJ until end3 step pow2Plus) {
                        val pos2 = posK + secondPow

                        val c1 = localAmplitudes[posK]
                        val c2 = localAmplitudes[pos2]

                        localAmplitudes[posK].real = c1.real * cosTheta + c2.complex * sinTheta
                        localAmplitudes[posK].complex = c1.complex * cosTheta - c2.real * sinTheta
                        localAmplitudes[pos2].real = c2.real * cosTheta + c1.complex * sinTheta
                        localAmplitudes[pos2].complex = c2.complex * cosTheta - c1.real * sinTheta
                    }
                }
            }

            return localAmplitudes
        }
        return emptyList()
    }

    fun handleH(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        val target = gate.targetQubit
        val control = 2.0.pow(target).toInt()
        val firstPlusPow = 2.0.pow(target + 1).toInt()
        val opposingPow = 2.0.pow(numberOfQubits - target - 1).toInt()

        val localAmplitudes = arrayListOf<ComplexNumber>()
        amplitudes.forEach { localAmplitudes.add(it.copy()) }

        for (i in 0 until control) {
            var posJ = 0
            for (j in 0 until opposingPow) {
                val pos1 = i + posJ
                val pos2 = pos1 + control

                val p1 = amplitudes[pos1]
                val p2 = amplitudes[pos2]

                localAmplitudes[pos1].real = (p1.real + p2.real) * MathConstants.Norm2
                localAmplitudes[pos1].complex = (p1.complex - p2.complex) * MathConstants.Norm2
                localAmplitudes[pos2].real = (p1.real + p2.real) * MathConstants.Norm2
                localAmplitudes[pos2].complex = (p1.complex - p2.complex) * MathConstants.Norm2

                posJ += firstPlusPow
            }
        }

        return localAmplitudes
    }

    fun handleM(amplitudes: List<ComplexNumber>, gate: Gate, numberOfQubits: Int): List<ComplexNumber> {
        return amplitudes
    }

}