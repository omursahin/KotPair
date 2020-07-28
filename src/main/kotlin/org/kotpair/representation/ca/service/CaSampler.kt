package org.kotpair.representation.ca.service

import com.google.inject.Inject
import org.kotpair.KPConfig
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.search.service.Sampler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.annotation.PostConstruct


class CaSampler : Sampler<CaIndividual>(){

    companion object {
        private val log: Logger = LoggerFactory.getLogger(CaSampler::class.java)
    }

     @Inject
    private lateinit var configuration: KPConfig

    @PostConstruct
    private fun initialize() {

        log.debug("Initializing {}", CaSampler::class.simpleName)
        //Read the input file
        File(configuration.parametersFile).useLines {
            val parameterList = it.toList().iterator()
            while (parameterList.hasNext()){
                print(parameterList.next())
            }
        }

        log.debug("Done initializing {}", CaSampler::class.simpleName)
    }

    override fun sampleAtRandom(): CaIndividual {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}