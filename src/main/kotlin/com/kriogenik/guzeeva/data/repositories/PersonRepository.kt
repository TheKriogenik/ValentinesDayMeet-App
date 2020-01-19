package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.Person
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRepository: CrudRepository<Person, Int>
