package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.Person
import java.util.*

interface PersonService {

    fun save(person: Person): Optional<Person>

    fun find(id: Long): Optional<Person>

    fun delete(person: Person)

}
