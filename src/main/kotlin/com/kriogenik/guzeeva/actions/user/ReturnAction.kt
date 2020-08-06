package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
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
class ReturnAction: Action<Person> {
    override val actionName: String = UserActions.RETURN.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.USER_RETURN_BUTTON)
    private lateinit var returnButtonText: String

    @StringResource(StringResources.BACK_TO_SYSTEM_ERROR)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.BACK_TO_SYSTEM_SUCCESS)
    private lateinit var successMessageText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            person.copy(state = PersonEntityState.ACTIVE).let(personService::update).map{
                successReturnMessage(it.vkId)
            }
        }.orElseGet{
            errorMessage(message.userVkId).let{ Optional.of(it) }
        }
    }

    private val successReturnMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = successMessageText
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
        ResponseMessage(vkId, "Вы успешно вернулись в систему!", Keyboard(
                listOf(
                        listOf(Key("В меню", UserActions.TO_MENU.toString(), Key.Color.POSITIVE))
                )
        ), listOf())
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
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