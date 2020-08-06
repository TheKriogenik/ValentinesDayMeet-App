package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
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
class ChangePreferenceAction: Action<Person> {

    override val actionName: String = UserActions.CHANGE_PREFERENCE.toString()

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_PREFERENCES)
    private lateinit var selectPreferenceText: String

    @StringResource(StringResources.PREFERENCE_MALE)
    private lateinit var malePreferenceText: String

    @StringResource(StringResources.PREFERENCE_FEMALE)
    private lateinit var femalePreferenceText: String

    @StringResource(StringResources.PREFERENCE_BOTH)
    private lateinit var bothPreferenceSexText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_SELECTED_PREFERENCE)
    private lateinit var preferenceSelectedText: String

    @StringResource(StringResources.PHOTO_REGISTRATION_FINISHED)
    private lateinit var photo: String

    @Autowired
    private lateinit var personService: PersonService

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            when(person.state){
                PersonEntityState.ACTIVE -> manualChanging(person)
                PersonEntityState.NOT_CREATED -> message.payload.map{
                    registrationChanging(person, it.args.first().let{Preference.valueOf(it)})
                }.orElseGet{
                    errorMessage(person.vkId)
                }
                else -> message.payload.map{
                    performChanging(person, it.args.first().let{Preference.valueOf(it)})
                }.orElseGet{
                    manualChanging(person)
                }
            }
        }
    }

    private fun registrationChanging(person: Person, preference: Preference): ResponseMessage{
        return person.copy(preference = preference, state = PersonEntityState.ACTIVE).let(personService::update).map{
            successRegistration(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun performChanging(person: Person, preference: Preference): ResponseMessage{
        return person.copy(state = PersonEntityState.ACTIVE, preference = preference)
                .let(personService::update).map{
                    successManualChange(it.vkId)
                }.orElseGet{
                    errorMessage(person.vkId)
                }
    }

    private fun manualChanging(person: Person): ResponseMessage{
        return person.copy(state = PersonEntityState.CHANGING_PREFERENCE, preference = Preference.NOT_SELECTED)
                .let(personService::update).map{
                    selectPreferenceMessage(it.vkId)
                }.orElseGet{
                    errorMessage(person.vkId)
                }
    }

    private val successRegistration = {vkId: Int ->
        successManualChange(vkId)
    }

    private val successManualChange = {vkId: Int ->
        message {
            userVkId = vkId
            text     = preferenceSelectedText
            keyboard = toMenuKeyboard
            attachments = listOf(photo)
        }
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
            keyboard = toMenuKeyboard
        }
    }

    private val selectPreferenceMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text = selectPreferenceText
            keyboard = KeyboardDsl.keyboard {
                row {
                    key {
                        text = malePreferenceText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.MALE}"
                        color = Key.Color.DEFAULT
                    }
                    key {
                        text = femalePreferenceText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.FEMALE}"
                        color = Key.Color.DEFAULT
                    }
                }
                row {
                    key {
                        text = bothPreferenceSexText
                        payload = "${UserActions.CHANGE_PREFERENCE}:${Preference.BOTH}"
                        color = Key.Color.DEFAULT
                    }
                }
            }
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