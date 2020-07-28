package org.kotpair.representation.ca.service

import com.google.inject.Inject
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.CaParameters
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.service.FitnessFunction

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct

class CaFitness : FitnessFunction<CaIndividual>() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CaFitness::class.java)
    }


    @Inject
    private lateinit var sampler: CaSampler

    @Inject
    private lateinit var param: CaParameters


    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", CaFitness::class.simpleName)

        log.debug("Done initializing {}", CaFitness::class.simpleName)
    }




    override fun doCalculateCoverage(individual: CaIndividual): EvaluatedIndividual<CaIndividual>? {


//TODO fitness hesabı burada yapılmalı

        val fv = FitnessValue(individual.size().toDouble())

        //TODO Setlerdeki unique değerlere bakıp saymak lazım.
        //8 değeri toplam parametre sayısı olmalı.
        val pairs = param.getTestCasePairs()

        pairs.iterator().forEach {
            val a = it
            println("1: ${a.first} 2: ${a.second}")
        }
        //TODO numpy benzeri bir yapi oluşturursam daha rahat unique hesaplayabilirim. Yoksa java pairwisedaki gibi yapmak lazim.
        return EvaluatedIndividual(fv, individual.copy() as CaIndividual)

        /*
            TODO when dealing with seeding, might want to extend EvaluatedIndividual
            to keep track of AdditionalInfo
         */
    }

}