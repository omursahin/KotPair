package org.kotpair

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.TypeLiteral
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.service.CaFitness
import org.kotpair.representation.ca.service.CaSampler
import org.kotpair.search.FitnessValue
import org.kotpair.search.Randomness
import org.kotpair.search.service.*


/**
 * When we were the application, there is going to a be a set of
 * default beans/services which are used regardless of the kind
 * of testing we do.
 */
class BaseModule(val args: Array<String>) : AbstractModule() {

    constructor() : this(emptyArray())

    override fun configure() {


        bind(SearchTimeController::class.java)
            .asEagerSingleton()

        bind(AdaptiveParameterControl::class.java)
            .asEagerSingleton()

        bind(Randomness::class.java)
                .asEagerSingleton()


        bind(SearchStatusUpdater::class.java)
            .asEagerSingleton()


    }

    @Provides @Singleton
    fun getKPConfig() : KPConfig{
        val config = KPConfig()

        val parser = KPConfig.getOptionParser()
        val options = parser.parse(*args)

        config.updateProperties(options)
        return config
    }
}