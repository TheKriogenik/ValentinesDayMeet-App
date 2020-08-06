package com.kriogenik.guzeeva.auth

import com.kriogenik.guzeeva.data.services.PersonRoleService
import com.kriogenik.guzeeva.model.PersonRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonAuthenticatorDefault: PersonAuthenticator {

    @Autowired
    private lateinit var personRoleService: PersonRoleService

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun auth(personId: Int): PersonRole.Role {
        log.debug("Authenticating Person#${personId}.")
        return personRoleService.getPersonRole(personId).map{
            it.role
        }.orElse(PersonRole.Role.NOT_AUTHENTICATED).also{
            log.debug("Person #${personId} role is $it.")
        }
    }

}
