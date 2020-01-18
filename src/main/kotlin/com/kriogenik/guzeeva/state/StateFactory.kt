package com.kriogenik.guzeeva.state

import com.kriogenik.guzeeva.model.EntityState

interface StateFactory<T> {

    fun getState(stateEnum: EntityState<T>): State<T>

}