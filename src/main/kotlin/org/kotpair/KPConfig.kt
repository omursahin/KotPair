package org.kotpair

import com.sun.org.apache.xml.internal.serialize.OutputFormat
import joptsimple.BuiltinHelpFormatter
import joptsimple.OptionDescriptor
import joptsimple.OptionParser
import joptsimple.OptionSet
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.javaType

class KPConfig {

    companion object {

        fun validateOptions(args: Array<String>): OptionParser {

            val config = KPConfig()

            val parser = KPConfig.getOptionParser()
            val options = parser.parse(*args)

            if (!options.has("help")) {
                //actual validation is done here
                config.updateProperties(options)
            }

            return parser
        }

        /**
         * Having issue with types/kotlin/reflection...
         * Therefore, need custom output formatting.
         * However, easier way (for now) is to just override
         * what we want to change
         *
         *  TODO: groups and ordering
         */
        private class MyHelpFormatter : BuiltinHelpFormatter(80, 2) {
            override fun extractTypeIndicator(descriptor: OptionDescriptor): String? {
                return null
            }
        }

        /**
         * Get all available "console options" for the annotated properties
         */
        fun getOptionParser(): OptionParser {

            val defaultInstance = KPConfig()

            var parser = OptionParser()

            parser.accepts("help", "Print this help documentation")
                .forHelp()

            getConfigurationProperties().forEach { m ->
                /*
                    Note: here we could use typing in the options,
                    instead of converting everything to string.
                    But it looks bit cumbersome to do it in Kotlin,
                    at least for them moment

                    Until we make a complete MyHelpFormatter, here
                    for the types we "hack" the default one, ie,
                    we set the type description to "null", but then
                    the argument description will contain the type
                 */

                val argTypeName = m.returnType.toString()
                    .run { substring(lastIndexOf('.') + 1) }

                parser.accepts(m.name, getDescription(m))
                    .withRequiredArg()
                    .describedAs(argTypeName)
                    .defaultsTo("" + m.call(defaultInstance))
            }

            parser.formatHelpWith(MyHelpFormatter())

            return parser
        }

        private fun getDescription(m: KMutableProperty<*>): String {

            val cfg = (m.annotations.find { it is Cfg } as? Cfg)
                ?: throw IllegalArgumentException("Property ${m.name} is not annotated with @Cfg")

            val text = cfg.description.trim().run {
                when {
                    isBlank() -> "No description."
                    !endsWith(".") -> this + "."
                    else -> this
                }
            }

            val min = (m.annotations.find { it is Min } as? Min)?.min
            val max = (m.annotations.find { it is Max } as? Max)?.max

            var constraints = ""
            if (min != null || max != null) {
                constraints += " [Constraints: "
                if (min != null) {
                    constraints += "min=$min"
                }
                if (max != null) {
                    if (min != null) constraints += ", "
                    constraints += "max=$max"
                }
                constraints += "]."
            }

            var enumValues = ""

            val returnType = m.returnType.javaType as Class<*>

            if (returnType.isEnum) {
                val elements = returnType.getDeclaredMethod("values")
                    .invoke(null) as Array<*>

                enumValues = " [Values: " + elements.joinToString(", ") + "]"
            }

            var description = "$text$constraints$enumValues"

            val experimental = (m.annotations.find { it is Experimental } as? Experimental)
            if(experimental != null){
                /*
                    TODO: For some reasons, coloring is not working here.
                    Could open an issue at:
                    https://github.com/jopt-simple/jopt-simple
                 */
                //description = AnsiColor.inRed("EXPERIMENTAL: $description")
                description = "EXPERIMENTAL: $description"
            }

            return description
        }


        fun getConfigurationProperties(): List<KMutableProperty<*>> {
            return KPConfig::class.members
                .filterIsInstance(KMutableProperty::class.java)
                .filter { it.annotations.any { it is Cfg } }
        }
    }

    /**
     * Update the values of the properties based on the options
     * chosen on the command line
     *
     *
     * @throws IllegalArgumentException if there are constraint violations
     */
    fun updateProperties(options: OptionSet) {

        getConfigurationProperties().forEach { m ->

            val opt = options.valueOf(m.name)?.toString() ?:
            throw IllegalArgumentException("Value not found for property ${m.name}")

            val returnType = m.returnType.javaType as Class<*>

            /*
                TODO: ugly checks. But not sure yet if can be made better in Kotlin.
                Could be improved with isSubtypeOf from 1.1?
                http://stackoverflow.com/questions/41553647/kotlin-isassignablefrom-and-reflection-type-checks
             */


            try {
                if (Integer.TYPE.isAssignableFrom(returnType)) {
                    m.setter.call(this, Integer.parseInt(opt))

                } else if (java.lang.Long.TYPE.isAssignableFrom(returnType)) {
                    m.setter.call(this, java.lang.Long.parseLong(opt))

                } else if (java.lang.Double.TYPE.isAssignableFrom(returnType)) {
                    m.setter.call(this, java.lang.Double.parseDouble(opt))

                } else if (java.lang.Boolean.TYPE.isAssignableFrom(returnType)) {
                    m.setter.call(this, java.lang.Boolean.parseBoolean(opt))

                } else if (java.lang.String::class.java.isAssignableFrom(returnType)) {
                    m.setter.call(this, opt)

                } else if (returnType.isEnum) {
                    val valueOfMethod = returnType.getDeclaredMethod("valueOf",
                        java.lang.String::class.java)
                    m.setter.call(this, valueOfMethod.invoke(null, opt))

                } else {
                    throw IllegalStateException("BUG: cannot handle type $returnType")
                }
            } catch (e: Exception) {
                throw IllegalArgumentException("Failed to handle property '${m.name}'", e)
            }

            val parameterValue = m.getter.call(this).toString()

            m.annotations.find { it is Min }?.also {
                it as Min
                if(parameterValue.toDouble() < it.min){
                    throw IllegalArgumentException("Failed to handle Min ${it.min} constraint for" +
                            " parameter '${m.name}' with value $parameterValue")
                }
            }

            m.annotations.find { it is Max }?.also {
                it as Max
                if(parameterValue.toDouble() > it.max){
                    throw IllegalArgumentException("Failed to handle Max ${it.max} constraint for" +
                            " parameter '${m.name}' with value $parameterValue")
                }
            }
        }

        when(stoppingCriterion){
            StoppingCriterion.TIME -> if(maxActionEvaluations != defaultMaxActionEvaluations){
                throw IllegalArgumentException("Changing number of max actions, but stopping criterion is time")
            }
            StoppingCriterion.FITNESS_EVALUATIONS -> if(maxTimeInSeconds != defaultMaxTimeInSeconds){
                throw IllegalArgumentException("Changing number of max seconds, but stopping criterion is based on fitness evaluations")
            }
        }


    }


    fun experimentalFeatures() : List<String>{

        val properties = getConfigurationProperties()
            .filter { it.annotations.find { it is Experimental } != null }
            .filter {
                val returnType = it.returnType.javaType as Class<*>
                if(java.lang.Boolean.TYPE.isAssignableFrom(returnType)){
                    it.getter.call(this) as Boolean
                } else {
                    false
                }
            }
            .map { it.name }

        val enums = getConfigurationProperties()
            .filter {
                val returnType = it.returnType.javaType as Class<*>
                if (returnType.isEnum){
                    val e = it.getter.call(this)
                    val f = returnType.getField(e.toString())
                    f.annotations.find { it is Experimental } != null
                } else {
                    false
                }
            }
            .map { "${it.name}=${it.getter.call(this)}" }

        return properties.plus(enums)
    }

//------------------------------------------------------------------------
//--- custom annotations

    /**
     * Configuration (CFG in short) for EvoMaster.
     * Properties annotated with [Cfg] can be set from
     * command line.
     * The code in this class uses reflection, on each property
     * marked with this annotation, to build the list of available
     * modifiable configurations.
     */
    @Target(AnnotationTarget.PROPERTY)
    @MustBeDocumented
    annotation class Cfg(val description: String)

    @Target(AnnotationTarget.PROPERTY)
    @MustBeDocumented
    annotation class Min(val min: Double)

    @Target(AnnotationTarget.PROPERTY)
    @MustBeDocumented
    annotation class Max(val max: Double)

    /**
     * This annotation is used to represent properties controlling
     * features that are still work in progress.
     * Do not use them (yet) in production.
     */
    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
    @MustBeDocumented
    annotation class Experimental

//------------------------------------------------------------------------
//--- properties

    enum class Algorithm {
        ABC
    }

    @Cfg("The algorithm used to generate test cases")
    var algorithm = Algorithm.ABC

    enum class RepresentationType {
        COVERING_ARRAY
    }

    @Cfg("The type of SUT we want to generate tests for, e.g., a RESTful API")
    var representationType = RepresentationType.COVERING_ARRAY


    @Cfg("The seed for the random generator used during the search. " +
            "A negative value means the CPU clock time will be rather used as seed")
    var seed: Long = -1


    enum class StoppingCriterion {
        TIME,
        FITNESS_EVALUATIONS
    }

    @Cfg("Stopping criterion for the search")
    var stoppingCriterion = StoppingCriterion.FITNESS_EVALUATIONS


    val defaultMaxActionEvaluations = 100

    @Cfg("Maximum number of action evaluations for the search." +
            " A fitness evaluation can be composed of 1 or more actions," +
            " like for example REST calls or SQL setups." +
            " The more actions are allowed, the better results one can expect." +
            " But then of course the test generation will take longer." +
            " Only applicable depending on the stopping criterion.")
    @Min(1.0)
    var maxActionEvaluations = defaultMaxActionEvaluations


    val defaultMaxTimeInSeconds = 60

    @Cfg("Maximum number of seconds allowed for the search." +
            " The more time is allowed, the better results one can expect." +
            " But then of course the test generation will take longer." +
            " Only applicable depending on the stopping criterion.")
    @Min(1.0)
    var maxTimeInSeconds = defaultMaxTimeInSeconds

    @Cfg("Where the parameters file is in")
    var parametersFile = "parameters.txt"

    @Cfg("Whether or not writing statistics of the search process. " +
            "This is only needed when running experiments with different parameter settings")
    var writeStatistics = true

    @Cfg("Where the statistics file (if any) is going to be written (in CSV format)")
    var statisticsFile = "statistics.csv"

    @Cfg("Whether should add to an existing statistics file, instead of replacing it")
    var appendToStatisticsFile = true

    @Cfg("If positive, check how often, in percentage % of the budget, to collect statistics snapshots." +
            " For example, every 5% of the time.")
    @Max(50.0)
    var snapshotInterval = 10.0

    @Cfg("Where the snapshot file (if any) is going to be written (in CSV format)")
    var snapshotStatisticsFile = "snapshot.csv"

    @Cfg("An id that will be part as a column of the statistics file (if any is generated)")
    var statisticsColumnId = "-"

    @Cfg("Define the population size in the search algorithms that use populations (e.g., Genetic Algorithms, but not MIO)")
    @Min(1.0)
    var populationSize = 50

    @Cfg("Define the maximum number of tests in a suite in the search algorithms that evolve whole suites, e.g. WTS")
    @Min(1.0)
    var maxSearchSuiteSize = 50

    @Cfg("Probability of applying crossover operation (if any is used in the search algorithm)")
    @Min(0.0) @Max(1.0)
    var xoverProbability = 0.7

    @Cfg("Number of elements to consider in a Tournament Selection (if any is used in the search algorithm)")
    @Min(1.0)
    var tournamentSize = 10

    @Cfg("The limit value for the ABC algorithms")
    @Min(0.0)
    var limit = 10


    @Cfg("When sampling new test cases to evaluate, probability of using some smart strategy instead of plain random")
    @Min(0.0) @Max(1.0)
    var probOfSmartSampling = 0.5

    @Cfg("Min number of 'test case' that can be done in a single test")
    @Min(1.0)
    var minTestSize = 2

    @Cfg("Max number of 'test case' that can be done in a single test")
    @Min(1.0)
    var maxTestSize = 5

    @Cfg("Whether to print how much search done so far")
    var showProgress = false

    @Experimental
    @Cfg("Whether or not enable a search process monitor for archiving evaluated individuals and Archive regarding an evaluation of search. "+
            "This is only needed when running experiments with different parameter settings")
    var enableProcessMonitor = false

    @Experimental
    @Cfg("Specify a folder to save results when a search monitor is enabled")
    var processFiles = "process_data"

    @Experimental
    @Cfg("Specify how often to save results when a search monitor is enabled ")
    var processInterval = 20



}
