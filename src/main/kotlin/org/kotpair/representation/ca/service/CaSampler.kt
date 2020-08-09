package org.kotpair.representation.ca.service

import com.google.inject.Inject
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.CaParameters
import org.kotpair.search.service.Sampler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct


class CaSampler : Sampler<CaIndividual>(){

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CaSampler::class.java)
    }

    @Inject
    protected lateinit var param: CaParameters

    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", CaSampler::class.simpleName)

        log.debug("Done initializing {}", CaSampler::class.simpleName)
    }

    override fun sampleAtRandom(): CaIndividual {
        val testCases = mutableListOf<IntArray>()
        val n = randomness.nextInt(config.minTestSize, config.maxTestSize)
        (0 until n).forEach {
            testCases.add(sampleRandomTestCase())
        }

        return CaIndividual(testCases)
    }

    fun sampleRandomTestCase(): IntArray{
        //TODO randomness sınıfına belirtilen dizi boyutunda ve maksimmum değerleri maxValArray kadar olan bir dizi olusturmasini sagla.
        val a = param.getMaxValArray()
        val testCase = randomness.nextTestCase(a)
        return testCase

     }
}