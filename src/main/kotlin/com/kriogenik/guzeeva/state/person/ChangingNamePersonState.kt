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
class ChangingNamePersonState: State<Person> {

    override val state: EntityState<Person> = PersonEntityState.CHANGING_NAME

    @Autowired
    private lateinit var service: PersonService

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(context: Person): Optional<Person> {
        log.debug("Changing state of Person#${context.vkId}.")
        return context.copy(state = PersonEntityState.CHANGING_NAME).let(service::update).map {
            log.debug("State of Person#${context.vkId} changed.")
            Optional.of(it)
        }.orElseGet{
            log.error("State of Person#${context.vkId} not changed!")
            Optional.empty<Person>()
        }
    }


}