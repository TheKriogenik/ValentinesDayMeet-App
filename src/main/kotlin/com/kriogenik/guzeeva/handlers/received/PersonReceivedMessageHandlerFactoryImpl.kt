package com.kriogenik.guzeeva.handlers.received

import com.kriogenik.guzeeva.model.PersonRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonReceivedMessageHandlerFactoryImpl: PersonReceivedMessageHandlerFactory {

    @Autowired
    private lateinit var personReceivedMessageHandlers: List<PersonReceivedMessageHandler>

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun getMessageHandlerByRole(role: PersonRole.Role): Optional<PersonReceivedMessageHandler> {
        log.debug("Searching for PersonReceivedMessageHandler by role $role.")
        return personReceivedMessageHandlers.find{ it.personRole == role }.let{ Optional.ofNullable(it) }.also{
            log.debug("Searching result: $it.")
        }
    }

}
