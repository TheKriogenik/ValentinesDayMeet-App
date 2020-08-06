package com.kriogenik.guzeeva.state

import com.kriogenik.guzeeva.model.EntityState
import java.util.*

interface State<T> {

    abstract val state: EntityState<T>

    fun execute(context: T): Optional<T>

}
