package org.kotpair.search




class EvaluatedIndividual<T>(val fitness: FitnessValue,
                             val individual: T
                             /**
                              * Note: as the test execution could had been
                              * prematurely stopped, there might be less
                              * results than actions
                              */
                            ) where T : Individual {
//TODO FitnessValue'lardan olusmali ve ikili sayisi kadar fitness value olmali. Ve burada toplamini (combined) vermeliyim
    init{

    }

    fun copy(): EvaluatedIndividual<T> {
        return EvaluatedIndividual(
            fitness.copy(),
            individual.copy() as T

        )
    }

}