package org.kotpair.representation.t_tuple

import org.kotpair.search.Individual


class TTupleIndividual(override val testCases:MutableList<IntArray>) : Individual() {

    override fun copy(): Individual {
        return TTupleIndividual(
            testCases.toMutableList()
        )
    }


    override fun size() = testCases.size
}