package org.kotpair.search.service.neighbourhood


import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * make the standard mutator open for extending the mutator,
 *
 * e.g., in order to handle resource rest individual
 */
open class GeneticNeighbour<T> : Neighbour<T>() where T : Individual {

    /**
     * List where each element at position "i" has value "2^i"
     */
    private val intpow2 = (0..30).map { Math.pow(2.0, it.toDouble()).toInt() }


//TODO genetik operatörleri eklemek lazım.
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

    override fun findNeighbours(individualOne: EvaluatedIndividual<T>,
                               individualTwo: EvaluatedIndividual<T>):Pair<T,T> {

        // First mutate the individual
        val individualToChangeOne = individualOne.individual
        // First mutate the individual
        val individualToChangeTwo = individualTwo.individual

        val newPair = crossover(individualToChangeOne,individualToChangeTwo)

       val ind1 = mutate(newPair.first,individualOne.fitness.getProbabilityMap(param))
       val ind2 = mutate(newPair.second,individualTwo.fitness.getProbabilityMap(param))
        //TODO kac defa hangi elemanlar degistirilecek ona bak.


        return Pair(ind1, ind2)

    }

    fun mutate(individualToChangeOne: T, probabilityMap: DoubleArray):T{
        val copy = individualToChangeOne.copy() as T
        if(randomness.nextBoolean(config.xoverProbability)) {

            val numberOfChanges = apc.getNumberOfChanges()

            (0 until numberOfChanges).forEach {
                val changeOne = randomness.nextInt(copy.size())
                val whichCase = probabilityMap.indexOf(probabilityMap.min()!!)
                copy.testCases[changeOne][whichCase] = randomness.nextInt(param.getMaxValArray()[whichCase])

            }
        }
        return copy
    }
    fun crossover(individualToChangeOne:T, individualToChangeTwo:T):Pair<T,T> {

        //TODO cunstruction failed exception gibi bir exception firlatmalisin. org.evosuite.ga.operators.crossover.signlepointcrossover

        val copyOne =  individualToChangeOne.copy() as T
        val copyTwo =  individualToChangeTwo.copy() as T
        if(randomness.nextBoolean(config.xoverProbability)) {

            val point = randomness.nextFloat()

            val pos1 = (floor((copyOne.size() - 1) * point).toInt()) + 1;
            val pos2 = (floor((copyTwo.size() - 1) * point).toInt()) + 1;

            while (copyOne.size() > pos1) {
                copyOne.testCases.removeAt(pos1)
            }

            for (i in pos2..copyTwo.size() - 1) {
                copyOne.testCases.add(individualToChangeTwo.testCases.get(i).clone())
            }

            while (copyTwo.size() > pos2) {
                copyTwo.testCases.removeAt(pos2)
            }

            for (i in pos1..individualToChangeOne.size() - 1) {
                copyTwo.testCases.add(individualToChangeOne.testCases.get(i).clone())
            }

        }

        //val listOne=copyOne.testCases.subList(0,pos1).addAll()
        //val listTwo = copyTwo.testCases.subList()

//TODO burada kaldim. Pozisyonlari belirledikten sonra test case alisverisi yapmaliyim.
        return Pair(copyOne, copyTwo)
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