package org.kotpair.search




class EvaluatedIndividual<T>(val fitness: FitnessValue,
                             val individual: T
                             /**
                              * Note: as the test execution could had been
                              * prematurely stopped, there might be less
                              * results than actions
                              */
                            ) where T : Individual {

    init{

    }

    fun copy(): EvaluatedIndividual<T> {
        return EvaluatedIndividual(
            fitness.copy(),
            individual.copy() as T

        )
    }

}