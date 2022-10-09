package org.kotpair.representation.t_tuple.service

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.TypeLiteral
import org.kotpair.KPConfig
import org.kotpair.representation.ca.TTupleParameters
import org.kotpair.representation.t_tuple.TTupleIndividual
import org.kotpair.search.Individual
import org.kotpair.search.service.FitnessFunction
import org.kotpair.search.service.Sampler
import org.kotpair.search.service.neighbourhood.DirectedNeighbour
import org.kotpair.search.service.neighbourhood.GeneticNeighbour
import org.kotpair.search.service.neighbourhood.Neighbour
import org.kotpair.search.service.neighbourhood.StandardNeighbour

class TTupleModule(val config: KPConfig) : AbstractModule(){


    override fun configure() {

        bind(object : TypeLiteral<Sampler<TTupleIndividual>>() {})
                .to(TTupleSampler::class.java)
                .asEagerSingleton()

        bind(TTupleSampler::class.java)
                .asEagerSingleton()

        bind(object : TypeLiteral<FitnessFunction<TTupleIndividual>>() {})
                .to(TTupleFitness::class.java)
                .asEagerSingleton()

        bind(TTupleParameters::class.java)
            .asEagerSingleton()

        val neighbourType = when (config.neighbourType) {
            KPConfig.Neighbour.STANDARD -> Key.get(object : TypeLiteral<StandardNeighbour<TTupleIndividual>>(){})
            KPConfig.Neighbour.DIRECTED -> Key.get(object : TypeLiteral<DirectedNeighbour<TTupleIndividual>>(){})
            KPConfig.Neighbour.GENETIC -> Key.get(object : TypeLiteral<GeneticNeighbour<TTupleIndividual>>(){})
            else -> throw IllegalStateException("Unrecognized neighbour type ${config.neighbourType}")
        }
        bind(object : TypeLiteral<Neighbour<TTupleIndividual>>() {})
            .to(neighbourType)
            .asEagerSingleton()
    }
}