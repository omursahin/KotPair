package org.kotpair.representation.ca

import org.kotpair.search.Individual


class CaIndividual(val testCases:MutableList<IntArray>) : Individual() {

    override fun copy(): Individual {
        return CaIndividual(
            testCases
        )
    }
    override fun size() = testCases.size
}