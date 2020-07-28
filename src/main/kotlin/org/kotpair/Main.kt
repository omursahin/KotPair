package org.kotpair

import com.google.inject.Injector
import com.google.inject.Key
import com.google.inject.TypeLiteral
import com.netflix.governator.guice.LifecycleInjector
import org.kotpair.AnsiColor.Companion.inBlue
import org.kotpair.AnsiColor.Companion.inGreen
import org.kotpair.AnsiColor.Companion.inRed
import org.kotpair.AnsiColor.Companion.inYellow
import org.kotpair.logging.LoggingUtil
import org.kotpair.representation.ca.CaIndividual
import org.kotpair.representation.ca.service.CaModule
import org.kotpair.search.Individual
import org.kotpair.search.algorithms.ABCAlgorithm
import java.lang.reflect.InvocationTargetException


/**
 * This will be the entry point of the tool when run from command line
 */
class Main {
    companion object {

        /**
         * Main entry point of the EvoMaster application
         */
        @JvmStatic
        fun main(args: Array<String>) {

            try {

                printLogo()
                printVersion()

                /*
                    Before running anything, check if the input
                    configurations are valid
                 */
                val parser = try {
                    KPConfig.validateOptions(args)
                } catch (e: Exception) {
                    logError("Invalid parameter settings: " + e.message +
                            "\nUse --help to see the available options")
                    return
                }

                if (parser.parse(*args).has("help")) {
                    parser.printHelpOn(System.out)
                    return
                }

                initAndRun(args)

                LoggingUtil.getInfoLogger().apply {
                    info("KotPair process has completed successfully")
                    info("Use ${inGreen("--help")}")
                }

            } catch (e: Exception) {

                var cause: Throwable = e
                while (cause.cause != null) {
                    cause = cause.cause!!
                }

                when (cause) {
                    else ->
                        LoggingUtil.getInfoLogger().error(inRed("[ERROR] ") +
                                inYellow("KotPair process terminated abruptly." +
                                        " This is likely a bug in KotPair."), e)
                }
            }
        }

        private fun logError(msg: String) {
            LoggingUtil.getInfoLogger().error(inRed("[ERROR] ") + inYellow(msg))
        }

        private fun logWarn(msg: String) {
            LoggingUtil.getInfoLogger().warn(inYellow("[WARNING] ") + inYellow(msg))
        }

        private fun printLogo() {

            val logo = """
             ||      ||     ||||||||||
             ||    ||       ||      ||
             ||  ||         ||      ||
             ||||           |||||||||
             ||  ||         ||
             ||    ||       ||
             ||      ||     ||  
               
                    """

            LoggingUtil.getInfoLogger().info(inBlue(logo))
        }

        private fun printVersion() {

            val version = this.javaClass.`package`?.implementationVersion ?: "unknown"

            LoggingUtil.getInfoLogger().info("KotPair version: $version")
        }

        @JvmStatic
        fun initAndRun(args: Array<String>) {

            val injector = init(args)

            checkExperimentalSettings(injector)


            val solution = run(injector)


            val config = injector.getInstance(KPConfig::class.java)



        }

        @JvmStatic
        fun init(args: Array<String>): Injector {

            val base = BaseModule(args)
            val representationType = base.getKPConfig().representationType

            val representationModule = when (representationType) {
                KPConfig.RepresentationType.COVERING_ARRAY-> CaModule()
                else -> throw IllegalStateException("Unrecognized representation type: $representationType")
            }
            try {
                return LifecycleInjector.builder()
                        .withModules(base, representationModule )
                        .build()
                        .createInjector()

            } catch (e: Error) {
                /*
                    Workaround to Governator bug:
                    https://github.com/Netflix/governator/issues/371
                 */
                if (e.cause != null &&
                        InvocationTargetException::class.java.isAssignableFrom(e.cause!!.javaClass)) {
                    throw e.cause!!
                }

                throw e
            }
        }


        fun run(injector: Injector) {

            //TODO check problem type
            //val rc = injector.getInstance(RemoteController::class.java)
            //rc.startANewSearch()

            val config = injector.getInstance(KPConfig::class.java)


           val key = when (config.algorithm) {
                KPConfig.Algorithm.ABC -> Key.get(
                        object : TypeLiteral<ABCAlgorithm<CaIndividual>>() {})


                else -> throw IllegalStateException("Unrecognized algorithm ${config.algorithm}")
            }

            val imp = injector.getInstance(key)

            LoggingUtil.getInfoLogger().info("Starting to generate test cases")
            val solution = imp.search()

        }

        private fun checkExperimentalSettings(injector: Injector) {

            val config = injector.getInstance(KPConfig::class.java)

            val experimental = config.experimentalFeatures()

            if (experimental.isEmpty()) {
                return
            }

            val options = "[" + experimental.joinToString(", ") + "]"

            logWarn("Using experimental settings." +
                    " Those might not work as expected, or simply straight out crash." +
                    " Furthermore, they might simply be incomplete features still under development." +
                    " Used experimental settings: $options")
        }
/*
        private fun checkState(injector: Injector): ControllerInfoDto {

            val rc = injector.getInstance(RemoteController::class.java)

            val dto = rc.getControllerInfo() ?: throw IllegalStateException(
                    "Cannot retrieve Remote Controller info from ${rc.host}:${rc.port}")

            if (dto.isInstrumentationOn != true) {
                LoggingUtil.getInfoLogger().warn("The system under test is running without instrumentation")
            }

            //TODO check if the type of controller does match the output format

            return dto
        }


        private fun writeTests(injector: Injector, solution: Solution<*>, controllerInfoDto: ControllerInfoDto) {

            val config = injector.getInstance(EMConfig::class.java)

            if (!config.createTests) {
                return
            }

            val n = solution.individuals.size
            val tests = if (n == 1) "1 test" else "$n tests"

            LoggingUtil.getInfoLogger().info("Going to save $tests to ${config.outputFolder}")

            val writer = injector.getInstance(TestSuiteWriter::class.java)

            writer.writeTests(
                    solution,
                    controllerInfoDto.fullName
            )
        }

        private fun writeStatistics(injector: Injector, solution: Solution<*>) {

            val config = injector.getInstance(EMConfig::class.java)

            if (!config.writeStatistics) {
                return
            }

            val statistics = injector.getInstance(Statistics::class.java)

            statistics.writeStatistics(solution)

            if (config.snapshotInterval > 0) {
                statistics.writeSnapshot()
            }
        }

        private fun writeOverallProcessData(injector: Injector) {

            val config = injector.getInstance(EMConfig::class.java)

            if (!config.enableProcessMonitor) {
                return
            }

            val process = injector.getInstance(SearchProcessMonitor::class.java)
            process.saveOverall()
        }*/
    }
}



