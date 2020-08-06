package com.kriogenik.guzeeva.auth

import com.kriogenik.guzeeva.model.PersonRole
import java.util.*

interface PersonAuthenticator {

    fun auth(personId: Int): PersonRole.Role

}
