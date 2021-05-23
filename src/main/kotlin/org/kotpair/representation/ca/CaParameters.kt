package org.kotpair.representation.ca

import com.google.inject.Inject
import org.kotpair.KPConfig
import org.kotpair.search.Parameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Integer.parseInt
import javax.annotation.PostConstruct


class CaParameters : Parameters() {

    private class Parameter(var maximumRange: Int,  var paramName:String) {

    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CaParameters::class.java)
    }

    @Inject
    private lateinit var configuration: KPConfig

    private var maximumNumberOfPair = 0.0

    private var parameters: MutableList<Parameter> = mutableListOf()

    private var maxValArray: MutableList<Int> = mutableListOf()

    private lateinit var testCasePairs:Sequence<Pair<Int,Int>>

    fun getMaxValArray()
    : MutableList<Int> {
        return maxValArray
    }

    fun getTestCasePairs(): Sequence<Pair<Int, Int>> {
        return testCasePairs
    }
    fun getMaximumNumberOfPair()=maximumNumberOfPair

    override fun copy(): Parameters {
        return CaParameters()
    }

    override fun size() = parameters.size

    @PostConstruct
    private fun initialize() {

        CaParameters.log.debug("Initializing {}", CaParameters::class.simpleName)
        //Read the input file
        File(configuration.parametersFile).useLines {
            val parameterList = it.toList().iterator()
            while (parameterList.hasNext()){
                var param = parameterList.next().split(":")
                for( i in 1..parseInt(param[1])) {
                    maxValArray.add(parseInt(param[0]))
                    parameters.add(Parameter(parseInt(param[0]),""))

                }
            }
        }
        testCasePairs = elementPairs(listOf(0..size()-1).flatten())
        maximumNumberOfPair = maximumPairNumber()
        CaParameters.log.debug("Done initializing {}", CaParameters::class.simpleName)
    }

    fun <T> elementPairs(arr: List<T>): Sequence<Pair<T, T>> = sequence {
        for(i in 0 until arr.size-1)
            for(j in i+1 until arr.size)
                yield(arr[i] to arr[j])
    }

    fun getPairIndexes(pairOne:Int): MutableList<Int> {
        val pairList = mutableListOf<Int>()
        val pairContains = testCasePairs.filter{it.first==pairOne||it.second==pairOne}
        pairContains.iterator().forEach {
            pairList.add(getTargetId(it.first,it.second))
        }
        return pairList
    }
    fun getTargetId(pairOne:Int, pairTwo:Int):Int{
        return testCasePairs.indexOf(Pair(pairOne,pairTwo))
    }

    fun maximumPairNumber(): Double {
        var maxPair = 0.0
        testCasePairs.iterator().forEach {
            maxPair += maxValArray[it.first]*maxValArray[it.second]
            }
        return maxPair
    }
}