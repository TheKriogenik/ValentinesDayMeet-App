package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.PersonRole
import java.util.*

interface PersonRoleService {

    fun getPersonRole(personId: Int): Optional<PersonRole>

    fun changePersonRole(personRole: PersonRole): Optional<PersonRole>

    fun addPersonRole(personRole: PersonRole): Optional<PersonRole>

}