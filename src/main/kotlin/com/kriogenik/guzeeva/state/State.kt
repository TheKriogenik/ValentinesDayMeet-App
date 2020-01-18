package com.kriogenik.guzeeva.state

import java.util.*

interface State<T> {

    fun execute(context: T): Optional<T>

}
