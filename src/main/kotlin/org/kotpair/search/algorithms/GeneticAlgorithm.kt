package org.kotpair.search.algorithms

import org.kotpair.KPConfig
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.Individual
import org.kotpair.search.Solution
import org.kotpair.search.service.SearchAlgorithm

class  GeneticAlgorithm<T> : SearchAlgorithm<T>() where T : Individual {



    override fun getType(): KPConfig.Algorithm {
        return KPConfig.Algorithm.GA
    }

    private var population: MutableList<EvaluatedIndividual<*>> = mutableListOf()


    override fun search(): Solution<T> {
        time.startSearch()
        population.clear()
        initPopulation(config.populationSize)

        while (time.shouldContinueSearch()) {

            population.forEachIndexed { index, pop ->
                if(time.shouldContinueSearch()){
                    val secondIndex = randomness.nextInt(population.size)
                    getNeighbour().findNeighbours(pop as EvaluatedIndividual<T>, population.get(secondIndex) as EvaluatedIndividual<T>)?.let {
                        ff.calculateCoverage(it.first)?.also { it2 ->
                            ff.calculateCoverage(it.second)?.also { it3 ->
                                val newParent = if(it3.fitness.computeFitnessScore() > it2.fitness.computeFitnessScore())  it3 else it2

                            if(newParent.fitness.computeFitnessScore()>population.get(index).fitness.computeFitnessScore()){
                                population.set(index,(newParent))
                            }
                            }
                        }
                    }

                }}
            //TODO bu raporlama kısmını raporlamaya aktar.
            println("First: ${population.get(0).fitness.computeFitnessScore()} - Second: ${population.get(population.size-1).fitness.computeFitnessScore()} - Max Val: ${ff.getMaxValue()}")
        }

        val uniques = mutableSetOf<EvaluatedIndividual<T>>()
        val overall = FitnessValue(1)
        return Solution(overall, uniques.toMutableList())
    }


    private fun initPopulation(n:Int) {

        for (i in 1..n) {
            sampleIndividual()?.run { population.add(this) }

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
