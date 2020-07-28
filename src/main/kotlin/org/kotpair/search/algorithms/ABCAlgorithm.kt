package org.kotpair.search.algorithms

import org.kotpair.KPConfig
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.Individual
import org.kotpair.search.Solution
import org.kotpair.search.service.SearchAlgorithm

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

        while(time.shouldContinueSearch()){

            time.newActionEvaluation()
        }
        val uniques = mutableSetOf<EvaluatedIndividual<T>>()
        val overall = FitnessValue(0.0)
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