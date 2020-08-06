package com.kriogenik.guzeeva.registration

import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.data.services.RegistrationCodeService
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonRegistrationManager: RegistrationManager<Person> {

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var registrationCodeService: RegistrationCodeService

    override fun register(registrationCode: String): Optional<PersonRole.Role> {
        log.debug("Attempt to register code $registrationCode.")
        return registrationCodeService.findCode(registrationCode).flatMap{existenceCode ->
            log.debug("Code is founded in base.")
            when(existenceCode.isActivated){
                true -> null
                else -> {
                    log.debug("Activating code...")
                    registrationCodeService.activateCode(registrationCode).map{
                        log.debug("Code activated.")
                        it.role
                    }.orElseGet{
                        log.debug("Something goes wrong.")
                        null
                    }
                }
            }.let{ Optional.ofNullable(it) }
        }
    }

}
