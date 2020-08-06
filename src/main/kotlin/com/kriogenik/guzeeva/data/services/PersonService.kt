package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.Sex
import java.util.*

interface PersonService {

    fun create(person: Person): Optional<Person>

    fun update(person: Person): Optional<Person>

    fun find(id: Int): Optional<Person>

    fun getAllBySex(sex: Sex): List<Person>

    fun getAllByState(state: PersonEntityState): List<Person>

    fun delete(person: Person)

}
