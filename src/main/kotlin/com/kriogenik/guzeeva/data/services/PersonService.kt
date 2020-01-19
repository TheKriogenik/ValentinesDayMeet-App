package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.Person
import java.util.*

interface PersonService {

    fun create(person: Person): Optional<Person>

    fun update(person: Person): Optional<Person>

    fun find(id: Int): Optional<Person>

    fun delete(person: Person)

}
