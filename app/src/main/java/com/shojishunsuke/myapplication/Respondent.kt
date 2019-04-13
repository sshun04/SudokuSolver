package com.shojishunsuke.myapplication

abstract class Respondent {
    abstract fun readQuestion()
    abstract fun trail()
    abstract fun printAnswer(): Array<Array<Int>>

    fun solve():Array<Array<Int>>{

        readQuestion()

        trail()

        return printAnswer()
    }


}