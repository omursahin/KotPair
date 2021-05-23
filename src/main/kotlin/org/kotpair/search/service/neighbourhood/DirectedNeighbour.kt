package org.kotpair.search.service.neighbourhood


import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import kotlin.math.roundToInt

/**
 * make the standard mutator open for extending the mutator,
 *
 * e.g., in order to handle resource rest individual
 */
open class DirectedNeighbour<T> : Neighbour<T>() where T : Individual {

    /**
     * List where each element at position "i" has value "2^i"
     */
    private val intpow2 = (0..30).map { Math.pow(2.0, it.toDouble()).toInt() }

    override fun findNeighbours(
        individualOne: EvaluatedIndividual<T>,
        individualTwo: EvaluatedIndividual<T>
    ): Pair<T, T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findNeighbour(individualOne: EvaluatedIndividual<T>,
                               individualTwo: EvaluatedIndividual<T>): T {
        // First mutate the individual
        val individualToChangeOne = individualOne.individual
//TODO burada targetlara gore komsuluk degistirme mekanizmasi olustur.

        val copy =  individualToChangeOne.copy() as T

        val numberOfChanges = apc.getNumberOfChanges()
        var probMap = individualOne.fitness.getProbabilityMap(param)
        //TODO burada prababilty map ihtiyacı var. Her bir pair için max value'ya bölünmüş fitness value toplamı gerekiyor.
        (0 until numberOfChanges).forEach{
            val changeOne = randomness.nextInt(copy.size())
            val changeTwo = randomness.nextInt(individualTwo.individual.size())

            copy.testCases[changeOne] = combineTestCases(copy.testCases.get(changeOne), individualTwo.individual.testCases.get(changeTwo),probMap)
            probMap = individualOne.fitness.getProbabilityMap(param)
//TODO her adımda probMap hesaplaması aşırı yük getiriyor.
        }
        //kac defa hangi elemanlar degistirilecek ona bak.


        return copy

    }
    fun combineTestCases(tc1: IntArray, tc2: IntArray, probMap: DoubleArray):IntArray{
        val testCase = IntArray(tc1.size)
        val maxValue = probMap.max()
        val minValue = probMap.min()
        var prob = 0.0
        (0 until tc1.size).forEach{
            //TODO bu kısım iyileşme sağlamadı

            prob = 1 - (probMap[it]-minValue!!)/(maxValue!!-minValue!!)
            testCase[it] = mod((tc1[it] + randomness.nextFloat()*(tc1[it]-tc2[it])).roundToInt(),param.getMaxValArray()[it])

        }
        return testCase
    }

    fun mod(x:Int, m:Int):Int{
        return (x%m+m)%m
    }

}