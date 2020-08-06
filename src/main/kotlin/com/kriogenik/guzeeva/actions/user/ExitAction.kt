package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ExitAction: Action<Person> {

    override val actionName: String = UserActions.EXIT.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.USER_EXIT_SUCCESS)
    private lateinit var exitText: String

    @StringResource(StringResources.USER_RETURN_BUTTON)
    private lateinit var returnButtonText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).flatMap{person ->
            person.copy(state = PersonEntityState.NOT_ACTIVE).let(personService::update).map{
                goodByeMessage(it.vkId)
            }
        }
    }

    private final val goodByeMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = exitText
            keyboard = keyboard {
                row {
                    key {
                        text    = returnButtonText
                        payload = UserActions.RETURN.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
