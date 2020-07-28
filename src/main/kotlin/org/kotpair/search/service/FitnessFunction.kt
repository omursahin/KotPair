package org.kotpair.search.service

import com.google.inject.Inject
import org.kotpair.KPConfig
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.Individual
import org.kotpair.search.Randomness
import java.util.concurrent.locks.ReentrantLock


abstract class FitnessFunction<T>  where T : Individual {

    @Inject
    protected lateinit var configuration: KPConfig


    @Inject
    protected lateinit var randomness : Randomness

    @Inject
    protected lateinit var time: SearchTimeController


    /**
     * @return [null] if there were problems in calculating the coverage
     */
    private val sharedCalculateLock = ReentrantLock()
    fun calculateCoverage(individual: T) : EvaluatedIndividual<T>?{

        /* while (lockProcess){
             println("MIO Sample Loop")
         }

         lockProcess=true
         */

        sharedCalculateLock.lock()
        var ei = doCalculateCoverage(individual)
        sharedCalculateLock.unlock()

        time.newActionEvaluation(1)
        time.newIndividualEvaluation()

        return ei
    }
    var lockProcess=false

    /**
     * @return [null] if there were problems in calculating the coverage
     */
    protected abstract fun doCalculateCoverage(individual: T) : EvaluatedIndividual<T>?

    /**
     * Try to reinitialize the SUT. This is done when there are issues
     * in calculating coverage
     */
    protected open fun reinitialize() = false
}