package org.kotpair.search


abstract class Parameters{

    /**
     * Make a deep copy of this individual
     */
    abstract fun copy(): Parameters

    /**
     * An estimation of the "size" of this individual.
     * Longer/bigger individuals are usually considered worse,
     * unless they cover more coverage targets
     */
    abstract fun size(): Int



}
