package org.kotpair.search.algorithms

import com.google.inject.Inject
import org.kotpair.KPConfig
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.Individual
import org.kotpair.search.Solution
import org.kotpair.search.service.SearchAlgorithm
import java.lang.Exception


class  ABCAlgorithm<T> : SearchAlgorithm<T>() where T : Individual {

    private class Data(val ind: EvaluatedIndividual<*>) {
        var trial = 0
    }

    private var population: MutableList<Data> = mutableListOf()

    override fun getType(): KPConfig.Algorithm {
        return KPConfig.Algorithm.ABC
    }


    override fun search(): Solution<T> {

        time.startSearch()
        population.clear()
        initPopulation(config.populationSize)


        var dongu=0
        var nextPop: MutableList<Data> = mutableListOf()

        while (time.shouldContinueSearch()) {
            dongu+=1

            // Employed Bee Phase
            population.forEachIndexed { index, pop ->
                if(time.shouldContinueSearch()){
                    val secondIndex = randomness.nextInt(population.size)
                    getNeighbour().findNeighbour(pop.ind as EvaluatedIndividual<T>, population.get(secondIndex).ind as EvaluatedIndividual<T>)?.let {
                        ff.calculateCoverage(it)?.also { it2 ->
                            if(it2.fitness.computeFitnessScore()>population.get(index).ind.fitness.computeFitnessScore()){
                                population.set(index,(Data(it2)))
                            }else {
                                population.get(index).trial += 1
                            }
                        }
                    }

                }}
            nextPop.addAll(population.sortedWith(compareBy { it.ind.fitness.computeFitnessScore() })
                .toMutableList())
            population.clear()
            population.addAll(nextPop)
            nextPop.clear()

            //Onlooker Bee Phase

            var popCount=0
            var dCount=0
            val maxVal = population.get(population.size-1).ind.fitness.computeFitnessScore()
            while (popCount<population.size && time.shouldContinueSearch())
            {


                val prob = 1-(population.get(dCount).ind.fitness.computeFitnessScore() * 0.9)/maxVal + 0.1

                if(randomness.nextBoolean(prob)){
                    val secondIndex = randomness.nextInt(population.size)

                    getNeighbour().findNeighbour(population.get(dCount).ind as EvaluatedIndividual<T>, population.get(secondIndex).ind as EvaluatedIndividual<T>)?.let {
                        ff.calculateCoverage(it)?.also { it2 ->
                            if(it2.fitness.computeFitnessScore()>population.get(dCount).ind.fitness.computeFitnessScore()){
                                population.set(dCount,(Data(it2)))
                            }else {
                                population.get(dCount).trial += 1
                            }
                        }
                    }
                    popCount+=1
                }

                dCount=(dCount+1)%(population.size-1)

            }
            nextPop.addAll(population.sortedWith(compareBy { it.ind.fitness.computeFitnessScore() })
                .toMutableList())
            population.clear()
            population.addAll(nextPop)
            nextPop.clear()

            // Scout Bee Phase
            popCount = 0
            while(popCount < population.size-1){
                if(population.get(popCount).trial>config.limit){
                    sampleIndividual()?.run {  population.set(popCount,Data(this))

                    }
                   continue
                }
                popCount+=1
                 }



            //TODO bu raporlama kısmını raporlamaya aktar.
            println("First: ${population.get(0).ind.fitness.computeFitnessScore()} - Second: ${population.get(population.size-1).ind.fitness.computeFitnessScore()} - Max Val: ${ff.getMaxValue()}")
        }
        val uniques = mutableSetOf<EvaluatedIndividual<T>>()
        val overall = FitnessValue(1)
        return Solution(overall, uniques.toMutableList())
    }


    private fun initPopulation(n:Int) {

        for (i in 1..n) {
            sampleIndividual()?.run { population.add(Data(this)) }

            if (!time.shouldContinueSearch()) {
                break
            }
        }
    }
    private fun sampleIndividual(): EvaluatedIndividual<T>? {
        val ind = if(sampler.hasSpecialInit()||randomness.nextBoolean(0.5)){
            time.type="Smart"
            sampler.smartSample()
        }
        else{
            time.type="Sample"
            sampler.sample()
        }
        return ff.calculateCoverage(ind)

    }
}