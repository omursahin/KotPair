package org.kotpair.representation.t_tuple.service

import com.google.inject.Inject
import com.sun.xml.internal.fastinfoset.util.StringArray
import org.kotpair.representation.ca.TTupleParameters
import org.kotpair.representation.t_tuple.TTupleIndividual
import org.kotpair.search.EvaluatedIndividual
import org.kotpair.search.FitnessValue
import org.kotpair.search.Heuristics
import org.kotpair.search.service.FitnessFunction

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct

class TTupleFitness : FitnessFunction<TTupleIndividual>() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TTupleFitness::class.java)
    }


    @Inject
    private lateinit var sampler: TTupleSampler

    @Inject
    private lateinit var param: TTupleParameters


    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", TTupleFitness::class.simpleName)

        log.debug("Done initializing {}", TTupleFitness::class.simpleName)
    }

    override fun doCalculateCoverage(individual: TTupleIndividual): EvaluatedIndividual<TTupleIndividual>? {
    /* Fitness hesabı burada gerceklestiriliyor. Column bazında kombinasyonlar hesaplaniyor ve her bir ikilinin farkli test durumlari toplaniyor. */

        val pairs = param.getTestCasePairs()
        val fv = FitnessValue(pairs.count())

        for ((index, it) in pairs.iterator().withIndex()) {
            fv.updateTarget(index,getUniqueNumber(individual.testCases,it.first,it.second),getMaxValueOfTarget(it.first,it.second))
        }

        return EvaluatedIndividual(fv, individual.copy() as TTupleIndividual)

    }

    override fun isMaxValue(value: Double) = value == param.getMaximumNumberOfPair()
    override fun getMaxValue() = param.getMaximumNumberOfPair()

    fun getMaxValueOfTarget(firstIndex:Int, secondIndex:Int):Double{
        return (param.getMaxValArray()[firstIndex]*param.getMaxValArray()[secondIndex]).toDouble()
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