package org.kotpair.search

import com.google.inject.Inject
import org.kotpair.representation.ca.CaParameters


/**
As the number of targets is unknown, we cannot have
a minimization problem, as new targets could be added
throughout the search
 */
class FitnessValue(
        /** An estimation of the size of the individual that obtained
         * this fitness value. Longer individuals are worse, but only
         * when fitness is not strictly better */
        var distance: Double) {


    init {
        if (distance < 0.0) {
            throw IllegalArgumentException("Invalid size value: $distance")
        }
    }



    fun copy(): FitnessValue {
             val copy = FitnessValue(distance)
             return copy
    }



}