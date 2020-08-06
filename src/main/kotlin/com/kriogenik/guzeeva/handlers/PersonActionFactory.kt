package com.kriogenik.guzeeva.handlers

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.model.Person
import java.util.*

interface PersonActionFactory {

    fun getPersonAction(payload: String): Optional<Action<Person>>

}