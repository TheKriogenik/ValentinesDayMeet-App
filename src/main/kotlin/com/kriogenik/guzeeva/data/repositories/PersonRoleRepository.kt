package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.PersonRole
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonRoleRepository: CrudRepository<PersonRole, Int> {
}