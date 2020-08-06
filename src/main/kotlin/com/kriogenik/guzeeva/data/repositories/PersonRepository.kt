package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.Sex
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: CrudRepository<Person, Int>{

    fun findBySex(sex: Sex): List<Person>

    fun findByState(state: PersonEntityState): List<Person>

}
