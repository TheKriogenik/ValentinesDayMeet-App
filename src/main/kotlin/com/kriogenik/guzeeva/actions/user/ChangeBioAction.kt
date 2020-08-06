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
import com.kriogenik.guzeeva.model.Preference
import com.kriogenik.guzeeva.model.Sex
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ChangeBioAction: Action<Person> {

    override val actionName: String = UserActions.CHANGE_BIO.toString()

    @StringResource(StringResources.REGISTRATION_ERROR_BIO_IS_EMPTY)
    private lateinit var emptyBioText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOU_BIO)
    private lateinit var enterYourBioText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toUserMenuButtonText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var changeBioErrorText: String

    @StringResource(StringResources.PREFERENCE_MALE)
    private lateinit var malePreferenceText: String

    @StringResource(StringResources.PREFERENCE_FEMALE)
    private lateinit var femalePreferenceText: String

    @StringResource(StringResources.PREFERENCE_BOTH)
    private lateinit var bothPreferenceSexText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_PREFERENCES)
    private lateinit var selectPreferenceText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_ENTER_BIO)
    private lateinit var bioChangedSuccessText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_SEX)
    private lateinit var selectSexText: String

    @StringResource(StringResources.SEX_MALE)
    private lateinit var sexMaleText: String

    @StringResource(StringResources.SEX_FEMALE)
    private lateinit var sexFemaleText: String

    @Autowired
    private lateinit var personService: PersonService

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            when(person.state){
                PersonEntityState.ACTIVE -> {
                    manualChanging(person)
                }
                PersonEntityState.NOT_CREATED -> {
                    filter(message).map{errorMessage ->
                        errorMessage
                    }.orElseGet{
                        registrationChanging(person, message.message)
                    }
                }
                else -> {
                    filter(message).map{errorMessage ->
                        errorMessage
                    }.orElseGet{
                        performChanging(person, message.message)
                    }
                }
            }
        }
    }

    private fun filter(message: ReceivedMessage): Optional<ResponseMessage>{
        return when{
            message.message.isEmpty() -> emptyBioMessage(message.userVkId).let{ Optional.of(it) }
            else                      -> Optional.empty()
        }
    }

    private fun registrationChanging(person: Person, bio: String): ResponseMessage{
        return person.copy(bio = bio).let(personService::update).map{
            successRegistration(person)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun manualChanging(person: Person): ResponseMessage{
        return person.copy(state = PersonEntityState.CHANGING_BIO, bio = "").let(personService::update).map{
            enterBioMessage(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun performChanging(person: Person, bio: String): ResponseMessage{
        return person.copy(state = PersonEntityState.ACTIVE, bio = bio).let(personService::update).map{
            successManualChanging(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private val emptyBioMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = emptyBioText
        }
    }

    private val enterBioMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = enterYourBioText
        }
    }

    private val successRegistration = { person: Person ->
        when(person.sex){
            Sex.NOT_SELECTED -> successRegistrationMessage(person.vkId)
            else -> selectPreferenceMessage(person.vkId)
        }
    }

    private val successRegistrationMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = "$bioChangedSuccessText\n$selectSexText"
            keyboard = keyboard {
                row {
                    key {
                        text    = sexMaleText
                        payload = "${UserActions.CHANGE_SEX}:${Sex.MALE}"
                        color   = Key.Color.DEFAULT
                    }
                    key {
                        text    = sexFemaleText
                        payload = "${UserActions.CHANGE_SEX}:${Sex.FEMALE}"
                        color   = Key.Color.DEFAULT
                    }
                }
            }
        }
    }

    private val successManualChanging = {vkId: Int ->
        message {
            userVkId = vkId
            text     = bioChangedSuccessText
            keyboard = toMenuKeyboard
        }
    }

    private val selectPreferenceMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = selectPreferenceText
            keyboard = keyboard {
                row {
                    key {
                        text    = malePreferenceText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.MALE}"
                        color   = Key.Color.DEFAULT
                    }
                    key {
                        text    = femalePreferenceText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.FEMALE}"
                        color   = Key.Color.DEFAULT
                    }
                }
                row {
                    key {
                        text    = bothPreferenceSexText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.BOTH}"
                        color   = Key.Color.DEFAULT
                    }
                }
            }
        }
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = changeBioErrorText
            keyboard = toMenuKeyboard
        }
    }

    private val toMenuKeyboard by lazy{
        keyboard{
            row{
                key{
                    text    = toUserMenuButtonText
                    payload = UserActions.TO_MENU.toString()
                    color   = Key.Color.POSITIVE
                }
            }
        }
    }

}
