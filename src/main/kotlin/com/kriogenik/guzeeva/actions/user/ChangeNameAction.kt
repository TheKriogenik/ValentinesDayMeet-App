package com.kriogenik.guzeeva.actions

import com.kriogenik.guzeeva.actions.user.UserActions
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
class ChangeNameAction: Action<Person> {

    override val actionName: String = UserActions.CHANGE_NAME.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.REGISTRATION_ERROR_NUMBERS_IN_NAME)
    private lateinit var errorNumbersInNameText: String

    @StringResource(StringResources.REGISTRATION_ERROR_NAME_IS_TOO_LONG)
    private lateinit var errorTooLongNameText: String

    @StringResource(StringResources.REGISTRATION_ERROR_NAME_IS_TOO_SHORT)
    private lateinit var errorTooShortNameText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_NAME)
    private lateinit var enterYourNameText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_ENTER_NAME)
    private lateinit var nameChangedText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_AGE)
    private lateinit var enterAgeText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            when(person.state){
                PersonEntityState.ACTIVE -> {
                    manualChanging(person)
                }
                else -> {
                    filter(message).map{errorMessage ->
                        errorMessage
                    }.orElseGet{
                        when(person.state){
                            PersonEntityState.CHANGING_NAME -> performChanging(person, message.message)
                            else                            -> registrationChanging(person, message.message)
                        }
                    }
                }
            }
        }
    }

    private fun filter(message: ReceivedMessage): Optional<ResponseMessage>{
        return when{
            message.message.isEmpty()          -> Optional.of(emptyName(message.userVkId))
            message.message.any(Char::isDigit) -> Optional.of(numbersInNameError(message.userVkId))
            message.message.length > 15        -> Optional.of(tooLongName(message.userVkId))
            message.message.length <= 2        -> Optional.of(tooShortName(message.userVkId))
            else -> Optional.empty()
        }
    }

    private fun registrationChanging(person: Person, name: String): ResponseMessage{
        return person.copy(name = name).let(personService::update).map{
            successRegistration(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun manualChanging(person: Person): ResponseMessage{
        return person.copy(state = PersonEntityState.CHANGING_NAME, name = "").let(personService::update).map{
            manualChangeNameMessage(person.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun performChanging(person: Person, name: String): ResponseMessage{
        return person.copy(state = PersonEntityState.CHANGING_NAME, name = name).let(personService::update).map{
            successManualChange(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private val manualChangeNameMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = enterYourNameText
        }
    }

    private val successRegistration = {vkId: Int ->
        message {
            userVkId = vkId
            text     = "$nameChangedText\n$enterAgeText"
        }
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
            keyboard = toMenuKeyboard
        }
    }

    private val successManualChange = {vkId: Int ->
        message {
            userVkId = vkId
            text     = nameChangedText
            keyboard = toMenuKeyboard
        }
    }

    private val emptyName = {vkId: Int ->
        message {
            userVkId = vkId
            text     = enterYourNameText
        }
    }

    private val tooLongName = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorTooLongNameText
        }
    }

    private val tooShortName = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorTooShortNameText
        }
    }

    private val numbersInNameError = { vkId: Int ->
        message {
            userVkId = vkId
            text     = errorNumbersInNameText
        }
    }

    private val toMenuKeyboard by lazy {
        keyboard {
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
