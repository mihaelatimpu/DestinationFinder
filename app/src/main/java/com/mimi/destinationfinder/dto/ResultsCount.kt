package com.mimi.destinationfinder.dto

/**
 * Created by Mimi on 19/11/2017.
 *
 */
class ResultsCount(val type:Int){
    companion object {
        val SEARCH_5 = 0
        val SEARCH_10 = 1
        val SEARCH_50 = 2
        fun default() = ResultsCount(SEARCH_5)
    }
    fun getMaxResults() =
            when(type){
                SEARCH_5 -> 5
                SEARCH_10 -> 10
                SEARCH_50 -> 50
                else ->
                    throw UnsupportedOperationException("unknown type: $type")
            }
}