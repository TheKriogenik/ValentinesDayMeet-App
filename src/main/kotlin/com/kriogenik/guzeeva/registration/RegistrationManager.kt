package com.kriogenik.guzeeva.registration

import com.kriogenik.guzeeva.model.PersonRole
import java.util.*

interface RegistrationManager<T>{

    fun register(registrationCode: String): Optional<PersonRole.Role>

}
