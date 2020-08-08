package app.bsc.db.drawing.data.prediction

class Result(probs: FloatArray, val timeCost: Long) {

    val number: Int
    private val probability: Float

    init {
        number = argmax(probs)
        probability = probs[number]
    }

    private fun argmax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIdx = i
            }
        }
        println(maxProb)
        return maxIdx
    }
}

