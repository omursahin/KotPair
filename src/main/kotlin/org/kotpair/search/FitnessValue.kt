package org.kotpair.search


/**
As the number of targets is unknown, we cannot have
a minimization problem, as new targets could be added
throughout the search
 */
class FitnessValue(
        /** An estimation of the size of the individual that obtained
         * this fitness value. Longer individuals are worse, but only
         * when fitness is not strictly better */
        var size: Double) {

    init {
        if (size < 0.0) {
            throw IllegalArgumentException("Invalid size value: $size")
        }
    }

    companion object {

        const val MAX_VALUE = 1.0

        fun isMaxValue(value: Double) = value == MAX_VALUE
    }

    fun copy(): FitnessValue {
             val copy = FitnessValue(size)
             return copy
    }



}