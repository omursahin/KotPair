package org.kotpair.search.algorithms

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

//            population.forEachIndexed { index, pop ->
//                if(time.shouldContinueSearch()){
//
//
//
//
//
//                    var ind = (pop.ind as EvaluatedIndividual<T>).copy()
//
////                    getMutatator().mutateAndSave(apc.getNumberOfMutations(),ind, archive)
////                        ?.let{
////                            var data = Data(it)
////                            if(!archive.added)
////                            {
////                                data.trial=population.get(index).trial+1
////
////                            }
////                            population.set(index,data)
////                        }
//
//                }
//            }
//
//                var popCount=0
//                var dCount=0
//                while (popCount<config.populationSize && time.shouldContinueSearch())
//                {
//                    var ind = archive.sampleIndividual()
//                    try {
//
//                        if(config.apcNumberOfMutationOnlooker)
//                            getMutatator().mutateAndSave(apc.getNumberOfMutationsABC(),ind,archive)?.let {if(archive.added)
//                            {population.set(dCount,Data(it))}}
//                        else
//                            getMutatator().mutateAndSave(ind,archive)?.let {if(archive.added)
//                            {population.set(dCount,Data(it))}}
//                        dCount=(dCount+1)%(population.size-1)
//                        popCount+=1
//
//                    }
//                    catch (e: Exception) {
//                        println("selam")
//
//                    }
//                }



            population.forEachIndexed { index, pop ->
                if(time.shouldContinueSearch()){
//                    if(pop.trial>config.limit)
//                    {
                       val sam=sampler.sample()
                        ff.calculateCoverage(sam)
                            ?.also {
                                if(it.fitness.distance>population.get(index).ind.fitness.distance){
                                population.set(index,(Data(it)))
                                }
                            }


//                    }
                }}
            nextPop.addAll(population.sortedWith(compareBy { it.ind.fitness.distance })
                .toMutableList())
            population.clear()
            population.addAll(nextPop)
            nextPop.clear()
            //TODO bu raporlama kısmını raporlamaya aktar.
            println("First: ${population.get(0).ind.fitness.distance} - Second: ${population.get(population.size-1).ind.fitness.distance} - Max Val: ${ff.getMaxValue()}")
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