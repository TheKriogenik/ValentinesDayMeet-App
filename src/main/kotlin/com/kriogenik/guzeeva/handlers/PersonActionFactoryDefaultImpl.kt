package com.kriogenik.guzeeva.handlers

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.model.Person
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonActionFactoryDefaultImpl: PersonActionFactory {

    @Autowired
    private lateinit var personActions: List<Action<Person>>

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun getPersonAction(payload: String): Optional<Action<Person>> {
        log.debug("Searching for PersonAction by name=$payload.")
        return personActions.find { it.actionName == payload }.let{ Optional.ofNullable(it) }.also{
            log.debug("Result: $it.")
        }
    }

}
