package org.kotpair.representation.ca

import org.kotpair.search.Parameters


class CaParameters : Parameters() {

    override fun copy(): Parameters {
        return CaParameters()
    }
    override fun size() = 0
}