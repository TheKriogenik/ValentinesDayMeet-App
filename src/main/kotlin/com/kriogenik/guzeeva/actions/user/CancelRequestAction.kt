package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.petersamokhin.bots.sdk.clients.Group
import com.petersamokhin.bots.sdk.objects.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class CancelRequestAction: Action<Person> {

    override val actionName: String = UserActions.CANCEL_REQUEST.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    @Autowired
    private lateinit var group: Group

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.USER_REQUEST_CANCELED_TARGET)
    private lateinit var requestCanceledText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
         return message.payload.flatMap{payload ->
             personService.find(message.userVkId).flatMap{initialPerson ->
                 payload.args.firstOrNull().let{ Optional.ofNullable(it) }.flatMap{targetPersonId ->
                     personService.find(targetPersonId.toInt()).map{targetPerson ->
                         cancelRequest(initialPerson, targetPerson)
                     }
                 }
             }
         }
    }

    private fun cancelRequest(initialPerson: Person, targetPerson: Person): ResponseMessage{
        return initialPerson.copy(state = PersonEntityState.ACTIVE)
                .let(personService::update).flatMap{
            targetPerson.copy(state = PersonEntityState.ACTIVE)
                    .let(personService::update).map{
                requestCanceledTarget(targetPerson.vkId).let(::sendMessageToTarget)
                requestCanceledInitial(initialPerson.vkId)
            }
        }.orElseGet{
            errorMessage(initialPerson.vkId)
        }
    }

    private fun sendMessageToTarget(message: ResponseMessage){
        responseMessageFactory.createResponseMessage(message).from(group).send()
    }

    private final val requestCanceledTarget = {vkId: Int ->
        message{
            userVkId = vkId
            text     = requestCanceledText
            keyboard = keyboard{
                row{
                    key{
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

    private final val requestCanceledInitial = {vkId: Int ->
        message{
            userVkId = vkId
            text     = requestCanceledText
            keyboard = keyboard{
                row{
                    key{
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

    private final val errorMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = errorText
            keyboard = keyboard {
                row {
                    key {
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
