package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import com.petersamokhin.bots.sdk.clients.Group
import com.petersamokhin.bots.sdk.objects.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class DeclineRequestAction: Action<Person> {

    override val actionName: String = UserActions.DECLINE_REQUEST.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorText: String

    @StringResource(StringResources.ANSWER_REQUEST_DECLINE_INITIAL)
    private lateinit var declineInitialText: String

    @StringResource(StringResources.ANSWER_REQUEST_DECLINE_TARGET)
    private lateinit var declineTargetText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.NEXT_PROFILE_BUTTON_TEXT)
    private lateinit var nextProfileText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).flatMap{targetPerson ->
            message.payload.flatMap{
                it.args.firstOrNull().let{ Optional.ofNullable(it) }.flatMap{initialId ->
                    personService.find(initialId.toInt()).map{initialPerson ->
                        declineRequest(initialPerson, targetPerson)
                    }
                }
            }
        }
    }

    private fun declineRequest(initialPerson: Person, targetPerson: Person): ResponseMessage{
        return initialPerson.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{
            targetPerson.copy(state = PersonEntityState.ACTIVE).let(personService::update).map{
                responseMessageFactory.createResponseMessage(declineInitialMessage(initialPerson.vkId, targetPerson.vkId))
                        .from(group)
                        .send()
                declineTargetMessage(initialPerson.vkId, targetPerson.vkId)
            }
        }.orElseGet{
            strangeError(targetPerson.vkId)
        }
    }

    private final val strangeError = {vkId: Int ->
        message {
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

    private val declineTargetMessage ={vkId: Int, prevId: Int ->
        message {
            userVkId = vkId
            text     = declineTargetText
            keyboard = profileKeyboard(prevId)
        }
    }

    private val declineInitialMessage ={vkId: Int, prevId: Int ->
        message {
            userVkId = vkId
            text     = declineInitialText
            keyboard = profileKeyboard(prevId)
        }
    }

    private val profileKeyboard = {prevId: Int ->
        keyboard {
            row {
                key {
                    text    = nextProfileText
                    payload = "${UserActions.SEARCH}:$prevId"
                    color   = Key.Color.POSITIVE
                }
            }
            row {
                key {
                    text    = toMenuButtonText
                    payload = UserActions.TO_MENU.toString()
                    color   = Key.Color.PRIMARY
                }
            }
        }
    }

}
