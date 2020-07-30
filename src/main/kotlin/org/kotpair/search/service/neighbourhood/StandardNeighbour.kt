package org.kotpair.search.service.neighbourhood


import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * make the standard mutator open for extending the mutator,
 *
 * e.g., in order to handle resource rest individual
 */
open class StandardNeighbour<T> : Neighbour<T>() where T : Individual {

    /**
     * List where each element at position "i" has value "2^i"
     */
    private val intpow2 = (0..30).map { Math.pow(2.0, it.toDouble()).toInt() }



    override fun findNeighbour(individual: EvaluatedIndividual<T>): T {

        // First mutate the individual
        val individualToMutate = individual.individual



        val copy =  individualToMutate.copy() as T

        //kac defa hangi elemanlar degistirilecek ona bak.


        return copy

    }

}