package org.kotpair.representation.ca.service

import com.google.inject.AbstractModule
import com.google.inject.TypeLiteral
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.search.service.FitnessFunction
import org.kotpair.search.service.Sampler

class CaModule : AbstractModule(){

    override fun configure() {
        bind(object : TypeLiteral<Sampler<CaIndividual>>() {})
                .to(CaSampler::class.java)
                .asEagerSingleton()

        bind(CaSampler::class.java)
                .asEagerSingleton()

        bind(object : TypeLiteral<FitnessFunction<CaIndividual>>() {})
                .to(CaFitness::class.java)
                .asEagerSingleton()


    }
}