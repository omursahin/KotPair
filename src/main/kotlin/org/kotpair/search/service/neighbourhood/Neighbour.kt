package org.kotpair.search.service.neighbourhood

import com.google.inject.Inject
import org.kotpair.KPConfig
import org.kotpair.representation.ca.CaParameters
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import org.kotpair.search.Randomness
import org.kotpair.search.service.AdaptiveParameterControl
import org.kotpair.search.service.FitnessFunction
import org.kotpair.search.service.SearchTimeController

abstract class Neighbour<T> where T : Individual {

    @Inject
    protected lateinit var randomness: Randomness

    @Inject
    protected lateinit var ff: FitnessFunction<T>

    @Inject
    protected lateinit var time: SearchTimeController

    @Inject
    protected lateinit var apc: AdaptiveParameterControl

    @Inject
    protected lateinit var param: CaParameters

    @Inject
    protected lateinit var config: KPConfig

    /**
     * @param mutatedGenes is used to record what genes are mutated within [mutate], which can be further used to analyze impacts of genes.
     * @return a mutated copy
     */
    abstract fun findNeighbour(individualOne: EvaluatedIndividual<T>,
                               individualTwo: EvaluatedIndividual<T>): T

    abstract fun findNeighbours(individualOne: EvaluatedIndividual<T>,
                               individualTwo: EvaluatedIndividual<T>): Pair<T,T>


}