package org.kotpair.representation.ca.service

import com.google.inject.Inject
import com.sun.xml.internal.fastinfoset.util.StringArray
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.CaParameters
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.service.FitnessFunction

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct

class CaFitness : FitnessFunction<CaIndividual>() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CaFitness::class.java)
    }


    @Inject
    private lateinit var sampler: CaSampler

    @Inject
    private lateinit var param: CaParameters


    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", CaFitness::class.simpleName)

        log.debug("Done initializing {}", CaFitness::class.simpleName)
    }

    override fun doCalculateCoverage(individual: CaIndividual): EvaluatedIndividual<CaIndividual>? {
    /* Fitness hesabı burada gerceklestiriliyor. Column bazında kombinasyonlar hesaplaniyor ve her bir ikilinin farkli test durumlari toplaniyor. */

        var fitness = 0.0

        val pairs = param.getTestCasePairs()
        pairs.iterator().forEach {
            fitness += getUniqueNumber(individual.testCases,it.first,it.second)
        }
        val fv = FitnessValue(fitness)

        return EvaluatedIndividual(fv, individual.copy() as CaIndividual)

    }

    fun getUniqueNumber(matrix: MutableList<IntArray>, firstIndex: Int, secondIndex: Int): Double {

        var firstColumn = getColumn(matrix, firstIndex)
        var secondColumn = getColumn(matrix, secondIndex)
        val mixedString =  arrayOfNulls<String>(firstColumn.size)

        for(i in 0 until firstColumn.size){
            mixedString[i]="${firstColumn[i]}-${secondColumn[i]}"
        }
        return mixedString.distinct().size.toDouble()
    }

    fun getColumn(matrix : MutableList<IntArray>, colNumber : Int):IntArray{
        val column = IntArray(matrix.size)
        matrix.forEachIndexed{index, element->
            column[index]=element[colNumber]
        }
        return column
    }

}