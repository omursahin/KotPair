package org.kotpair.representation.t_tuple.service

import com.google.inject.Inject
import org.kotpair.representation.ca.TTupleParameters
import org.kotpair.representation.t_tuple.TTupleIndividual
import org.kotpair.search.service.Sampler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct


class TTupleSampler : Sampler<TTupleIndividual>(){

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TTupleSampler::class.java)
    }

    @Inject
    protected lateinit var param: TTupleParameters

    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", TTupleSampler::class.simpleName)

        log.debug("Done initializing {}", TTupleSampler::class.simpleName)
    }

    override fun sampleAtRandom(): TTupleIndividual {
        val testCases = mutableListOf<IntArray>()
        val n = randomness.nextInt(config.minTestSize, config.maxTestSize)
        (0 until n).forEach {
            testCases.add(sampleRandomTestCase())
        }

        return TTupleIndividual(testCases)
    }

    fun sampleRandomTestCase(): IntArray{
        //TODO randomness sınıfına belirtilen dizi boyutunda ve maksimmum değerleri maxValArray kadar olan bir dizi olusturmasini sagla.
        val a = param.getMaxValArray()
        val testCase = randomness.nextTestCase(a)
        return testCase

     }
}