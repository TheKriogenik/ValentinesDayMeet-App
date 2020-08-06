package com.kriogenik.guzeeva.handlers

import java.util.*

interface Handler<IN, OUT> {

    fun handle(target: IN): Optional<OUT>

}
