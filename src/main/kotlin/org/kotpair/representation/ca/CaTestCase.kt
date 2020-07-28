package org.kotpair.representation.ca

import org.kotpair.search.TestCase

class CaTestCase(val test:IntArray) : TestCase(){

    override fun copy(): TestCase {
        return CaTestCase(
            test
        )
    }


}
