package org.kotpair.representation.ca.service

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.TypeLiteral
import org.kotpair.KPConfig
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.CaParameters
import org.kotpair.search.service.FitnessFunction
import org.kotpair.search.service.Sampler
import org.kotpair.search.service.neighbourhood.DirectedNeighbour
import org.kotpair.search.service.neighbourhood.GeneticNeighbour
import org.kotpair.search.service.neighbourhood.Neighbour
import org.kotpair.search.service.neighbourhood.StandardNeighbour

class CaModule(val config: KPConfig) : AbstractModule(){


    override fun configure() {
        bind(object : TypeLiteral<Sampler<CaIndividual>>() {})
                .to(CaSampler::class.java)
                .asEagerSingleton()

        bind(CaSampler::class.java)
                .asEagerSingleton()

        bind(object : TypeLiteral<FitnessFunction<CaIndividual>>() {})
                .to(CaFitness::class.java)
                .asEagerSingleton()

        bind(CaParameters::class.java)
            .asEagerSingleton()

        val neighbourType = when (config.neighbourType) {
            KPConfig.Neighbour.STANDARD -> Key.get(object : TypeLiteral<StandardNeighbour<CaIndividual>>(){})
            KPConfig.Neighbour.DIRECTED -> Key.get(object : TypeLiteral<DirectedNeighbour<CaIndividual>>(){})
            KPConfig.Neighbour.GENETIC -> Key.get(object : TypeLiteral<GeneticNeighbour<CaIndividual>>(){})
            else -> throw IllegalStateException("Unrecognized neighbour type ${config.neighbourType}")
        }
        bind(object : TypeLiteral<Neighbour<CaIndividual>>() {})
            .to(neighbourType)
            .asEagerSingleton()
    }
}