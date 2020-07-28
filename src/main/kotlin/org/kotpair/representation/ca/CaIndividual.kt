package org.kotpair.representation.ca

import org.kotpair.search.Individual


class CaIndividual : Individual() {

    override fun copy(): Individual {
        return CaIndividual()
    }
    override fun size() = 0
}