package org.kotpair.representation.ca

import org.kotpair.search.Individual


class CaIndividual(override val testCases:MutableList<IntArray>) : Individual() {

    override fun copy(): Individual {
        return CaIndividual(
            testCases.toMutableList()
        )
    }


    override fun size() = testCases.size
}