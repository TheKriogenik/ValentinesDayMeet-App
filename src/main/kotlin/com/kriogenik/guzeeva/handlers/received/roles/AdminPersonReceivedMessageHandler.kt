package com.kriogenik.guzeeva.handlers.received.roles

import com.kriogenik.guzeeva.actions.admin.AdminActions
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.handlers.PersonActionFactory
import com.kriogenik.guzeeva.handlers.received.PersonReceivedMessageHandler
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.PersonRole
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class AdminPersonReceivedMessageHandler: PersonReceivedMessageHandler {

    override val personRole: PersonRole.Role = PersonRole.Role.ADMIN

    @Autowired
    private lateinit var actionFactory: PersonActionFactory

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ADMIN_SUCCESS_REGISTRATION)
    private lateinit var successRegistrationText: String

    @StringResource(StringResources.HANDLER_NOT_FOUND)
    private lateinit var handlerNotFoundText: String

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(target: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $target")
        return personService.find(target.userVkId).map{person ->
            target.payload.flatMap{payload ->
                when(payload.action.actionName){
                    AdminActions.TO_ADMIN_MENU.toString() -> {
                        person.copy(state = PersonEntityState.ACTIVE_ADMIN).let(personService::update).flatMap{
                            payload.action.perform(target)
                        }
                    }
                    else -> {
                        log.info("PERFORM ACTION: ${payload.action.actionName}")
                        payload.action.perform(target)
                    }
                }
            }.orElseGet{
                log.info("PERSON: $person")
                when(person.state) {

                    PersonEntityState.ENTERING_CLOSE_TABLE_CODE -> {
                        actionFactory.getPersonAction(AdminActions.REMOVE_TABLE.toString()).flatMap {
                            it.perform(target)
                        }.orElseGet{
                            errorHandlingMessage(target.userVkId)
                        }
                    }
                    PersonEntityState.ENTERING_FREE_TABLE_CODE -> {
                        log.info("ENTERING_FREE_TABLE_CODE")
                        actionFactory.getPersonAction(AdminActions.FREE_TABLE.toString()).flatMap{
                            it.perform(target)
                        }.orElseGet{
                            errorHandlingMessage(target.userVkId)
                        }
                    }
                    PersonEntityState.ENTERING_NEW_TABLE_CODE -> {
                        log.info("ENTERING_NEW_TABLE_CODE")
                        actionFactory.getPersonAction(AdminActions.ADD_TABLE.toString()).flatMap{
                            it.perform(target)
                        }.orElseGet{
                            errorHandlingMessage(target.userVkId)
                        }
                    }
                    else -> toMenuMessage(target.userVkId)
                }
            }
        }
    }

    private val toMenuMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = successRegistrationText
            keyboard = keyboard {
                row{
                    key{
                        text    = toMenuButtonText
                        payload = AdminActions.TO_ADMIN_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

    private val errorHandlingMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = handlerNotFoundText
            keyboard = keyboard {
                row {
                    key {
                        text    = toMenuButtonText
                        payload = AdminActions.TO_ADMIN_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
