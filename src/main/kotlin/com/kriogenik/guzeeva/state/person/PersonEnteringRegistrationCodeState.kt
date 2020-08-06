package com.kriogenik.guzeeva.state.person

import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.model.EntityState
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.state.State
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonEnteringRegistrationCodeState: State<Person> {

    override val state: EntityState<Person> = PersonEntityState.ENTERING_REGISTRATION_CODE

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var personService: PersonService

    override fun execute(context: Person): Optional<Person> {
        log.debug("Changing state of Person#${context.vkId}.")
        return context.copy(state = PersonEntityState.ENTERING_REGISTRATION_CODE)
                .let(personService::create).map{
            Optional.of(it)
        }.orElseGet{
            Optional.empty()
        }
    }
}