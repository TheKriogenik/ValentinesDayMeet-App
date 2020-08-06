package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ChangeAgeAction: Action<Person> {

    override val actionName: String = UserActions.CHANGE_AGE.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_AGE)
    private lateinit var enterAgeText: String

    @StringResource(StringResources.REGISTRATION_ERROR_AGE_IS_TOO_YOUNG)
    private lateinit var ageIsTooYoungText: String

    @StringResource(StringResources.REGISTRATION_ERROR_AGE_IS_TOO_OLD)
    private lateinit var ageIsTooOldText: String

    @StringResource(StringResources.REGISTRATION_ERROR_LETTERS_IN_AGE)
    private lateinit var lettersInAgeText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_ENTER_AGE)
    private lateinit var ageSuccessText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOU_BIO)
    private lateinit var enterBioText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            when(person.state){
                PersonEntityState.ACTIVE -> {
                    manualChanging(person)
                }
                else -> filter(message).orElseGetOptional{
                    when(person.state){
                        PersonEntityState.NOT_CREATED -> registrationChanging(person, message.message)
                        else                          -> performChanging(person, message.message)
                    }
                }
            }.orElseGet{
                errorMessage(message.userVkId)
            }
        }
    }

    private fun filter(message: ReceivedMessage): Optional<ResponseMessage>{
        return message.message.let{ text ->
            when{
                text.any(Char::isLetter)  -> Optional.of(usedLettersMessage(message.userVkId))
                text.length <= 2          -> Optional.of(tooYoungMessage(message.userVkId))
                text.length >  3          -> Optional.of(tooOldMessage(message.userVkId))
                text.toInt() in (16..100) -> Optional.empty()
                else                      -> Optional.of(tooYoungMessage(message.userVkId))
            }
        }
    }

    private fun performChanging(person: Person, age: String): Optional<ResponseMessage>{
        return person.copy(state = PersonEntityState.ACTIVE, age = age)
                .let(personService::update).map{
            successManualChange(it.vkId)
        }
    }

    private fun manualChanging(person: Person): Optional<ResponseMessage>{
        return person.copy(state = PersonEntityState.CHANGING_AGE, age = "")
                .let(personService::update).map{
            enterAgeMessage(it.vkId)
        }
    }

    private fun registrationChanging(person: Person, age: String): Optional<ResponseMessage>{
        return person.copy(age = age).let(personService::update).map{
            successAgeRegistration(it.vkId)
        }
    }

    private val enterAgeMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = enterAgeText
        }
    }

    private val tooYoungMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = ageIsTooYoungText
        }
    }

    private val tooOldMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = ageIsTooOldText
        }
    }

    private val usedLettersMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = lettersInAgeText
        }
    }

    private val successAgeRegistration = {vkId: Int ->
        message{
            userVkId = vkId
            text     = ageSuccessText + "\n" + enterBioText
        }
    }

    private val successManualChange = {vkId: Int ->
        message {
            userVkId = vkId
            text     = ageSuccessText
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

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorText
            keyboard = keyboard{
                row {
                    key{
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

    private fun <T> Optional<T>.orElseGetOptional(block: Optional<T>.() -> Optional<T>) = block(this)

}
