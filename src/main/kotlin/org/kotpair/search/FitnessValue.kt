package org.kotpair.search

import com.google.inject.Inject
import org.kotpair.representation.ca.CaParameters



class FitnessValue(

        var size: Int) {


    init {
        if (size < 0.0) {
            throw IllegalArgumentException("Invalid size value: $size")
        }
    }


    private val targets: MutableMap<Int, Heuristics> = mutableMapOf()


    fun copy(): FitnessValue {
        val copy = FitnessValue(size)
        copy.targets.putAll(this.targets)
        return copy
    }

    fun getHeuristic(target: Int): Double = targets[target]?.distance ?: 0.0


    fun computeFitnessScore(): Double {

        return targets.values.map { h -> h.distance }.sum()
    }

    fun updateTarget(id: Int, value: Double, maxValue: Double) {

        if (value < 0 ) {
            throw IllegalArgumentException("Invalid value: $value")
        }

        targets[id] = Heuristics(value, maxValue)
    }
// Her bir değerin max valuelarını ayarlamak lazım
//      Belki kullanilabilir her bir hedef ayri ayri dusunulebilirse
//    fun coveredTargets(): Int {
//
//        return targets.values.filter { t -> t.distance == MAX_VALUE }.count()
//    }

}