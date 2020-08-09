package org.kotpair.search.service.neighbourhood


import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

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



    override fun findNeighbour(individualOne: EvaluatedIndividual<T>,
                               individualTwo: EvaluatedIndividual<T>): T {

        // First mutate the individual
        val individualToChangeOne = individualOne.individual



        val copy =  individualToChangeOne.copy() as T

        val numberOfChanges = apc.getNumberOfChanges()
        (0 until numberOfChanges).forEach{
            val changeOne = randomness.nextInt(copy.size())
            val changeTwo = randomness.nextInt(individualTwo.individual.size())
            copy.testCases[changeOne] = combineTestCases(copy.testCases.get(changeOne), individualTwo.individual.testCases.get(changeTwo))
        }
        //kac defa hangi elemanlar degistirilecek ona bak.


        return copy

    }
    fun combineTestCases(tc1:IntArray,tc2:IntArray):IntArray{
        val testCase = IntArray(tc1.size)
        (0 until tc1.size).forEach{
            testCase[it] = mod((tc1[it] + randomness.nextFloat()*(tc1[it]-tc2[it])).roundToInt(),param.getMaxValArray()[it])
        }
        return testCase
    }

    fun mod(x:Int, m:Int):Int{
        return (x%m+m)%m
    }

}