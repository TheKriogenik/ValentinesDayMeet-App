package com.kriogenik.guzeeva.handlers.received.roles

import com.kriogenik.guzeeva.actions.notauthenticated.NotAuthenticatedPersonActions
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.handlers.PersonActionFactory
import com.kriogenik.guzeeva.handlers.received.PersonReceivedMessageHandler
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.PersonRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class NotAuthenticatedReceivedMessageHandler: PersonReceivedMessageHandler {

    override val personRole = PersonRole.Role.NOT_AUTHENTICATED

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var actionFactory: PersonActionFactory

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(target: ReceivedMessage): Optional<ResponseMessage> {
        log.debug("Started handling of message $target.")
        return personService.find(target.userVkId).map{
            target.payload.map{
                it.action
            }
        }.orElseGet{
            actionFactory.getPersonAction(NotAuthenticatedPersonActions.SHOW_REGISTRATION.toString())
        }.map{action ->
            action.perform(target)
        }.orElseGet{
            actionFactory.getPersonAction(NotAuthenticatedPersonActions.PERFORM_REGISTRATION.toString()).flatMap {
                it.perform(target)
            }
        }
    }

}
