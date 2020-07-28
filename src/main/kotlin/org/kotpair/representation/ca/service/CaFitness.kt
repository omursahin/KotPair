package org.kotpair.representation.ca.service

import com.google.inject.Inject
import org.kotpair.representation.ca.CaIndividual
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


    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", CaFitness::class.simpleName)

        log.debug("Done initializing {}", CaFitness::class.simpleName)
    }




    override fun doCalculateCoverage(individual: CaIndividual): EvaluatedIndividual<CaIndividual>? {




        val fv = FitnessValue(individual.size().toDouble())
        return EvaluatedIndividual(fv, individual.copy() as CaIndividual)

        /*
            TODO when dealing with seeding, might want to extend EvaluatedIndividual
            to keep track of AdditionalInfo
         */
    }

}