package com.kriogenik.guzeeva.state

import com.kriogenik.guzeeva.model.EntityState
import java.util.*

interface StateFactory<T> {

    fun getState(stateEnum: EntityState<T>): Optional<State<T>>

}