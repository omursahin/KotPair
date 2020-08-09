package org.kotpair.search.service

import com.google.inject.Inject
import org.kotpair.KPConfig
import kotlin.math.roundToInt

/**
 * Search algorithm parameters might change during the search,
 * eg based on time or fitness feedback
 */
class AdaptiveParameterControl {

    @Inject
    private lateinit var time : SearchTimeController

    @Inject
    private lateinit var config: KPConfig


    fun getNumberOfChanges(): Int {
        return getExploratoryValue(config.startNumberOfChanges, config.endNumberOfChanges )
    }

    /**
     * Based on the current state of the search, ie how long has been passed
     * and how much budget is left before starting a focused search,
     * return  a value between [start] (at the beginning of the search) and [end]
     * (when the focused search starts)
     */
    fun getExploratoryValue(start: Int, end: Int) : Int{
        return Math.round(getExploratoryValue(start.toDouble(), end.toDouble())).toInt()
    }

    /**
     * Based on the current state of the search, ie how long has been passed
     * and how much budget is left before starting a focused search,
     * return  a value between [start] (at the beginning of the search) and [end]
     * (when the focused search starts)
     */
    fun getExploratoryValue(start: Double, end: Double) : Double{

        val passed: Double = time.percentageUsedBudget()
        val threshold:Double = config.focusedSearchActivationTime

        if(passed >= threshold){
            return end
        }

        val scale = passed / threshold

        val delta = end - start

        return start + (scale * delta)
    }

}