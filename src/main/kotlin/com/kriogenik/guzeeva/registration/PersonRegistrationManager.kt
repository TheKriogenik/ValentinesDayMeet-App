package com.kriogenik.guzeeva.registration

import com.kriogenik.guzeeva.data.services.RegistrationCodeService
import com.kriogenik.guzeeva.model.Person
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PersonRegistrationManager: RegistrationManager<Person> {

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var registrationCodeService: RegistrationCodeService

    override fun register(registrationCode: String): Boolean {
        log.debug("Attempt to register code $registrationCode.")
        return registrationCodeService.findCode(registrationCode).map{existenceCode ->
            log.debug("Code is founded in base.")
            when(existenceCode.isActivated){
                true -> false
                else -> {
                    log.debug("Activating code...")
                    registrationCodeService.activateCode(registrationCode).map{
                        log.debug("Code activated.")
                        true
                    }.orElseGet{
                        log.debug("Something goes wrong.")
                        false
                    }
                }
            }
        }.orElseGet{
            log.debug("Code not founded in base.")
            false
        }
    }

}
