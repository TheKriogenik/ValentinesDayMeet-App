package com.kriogenik.guzeeva.state

interface State<T> {

    fun execute(context: T)

}
