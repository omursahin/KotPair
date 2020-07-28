package org.kotpair.search



abstract class Individual{

    /**
     * Make a deep copy of this individual
     */
    abstract fun copy(): Individual

    /**
     * An estimation of the "size" of this individual.
     * Longer/bigger individuals are usually considered worse,
     * unless they cover more coverage targets
     */
    abstract fun size(): Int



}

