package org.kotpair.search.service

import com.google.inject.Inject
import org.kotpair.search.Individual
import org.kotpair.KPConfig
import org.kotpair.search.Randomness
import org.kotpair.search.Solution


abstract class SearchAlgorithm<T> where T : Individual {

//    @Inject
//    protected lateinit var sampler : AbstractSampler<T>
//
//    @Inject
//    protected lateinit var ff : AbstractFitnessFunction<T>

    @Inject
    protected lateinit var randomness : Randomness

    @Inject
    protected lateinit var ff : FitnessFunction<T>

    @Inject
    lateinit var time : SearchTimeController

    @Inject
    protected lateinit var sampler : Sampler<T>

    @Inject
    protected lateinit var config: KPConfig


    abstract fun search() : Solution<T>

    abstract fun getType() : KPConfig.Algorithm
}
