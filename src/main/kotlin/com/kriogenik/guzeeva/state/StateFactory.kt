package com.kriogenik.guzeeva.state

import com.kriogenik.guzeeva.model.EntityState

interface StateFactory<T> {

    fun changeState(stateEnum: EntityState<T>): State<T>

}