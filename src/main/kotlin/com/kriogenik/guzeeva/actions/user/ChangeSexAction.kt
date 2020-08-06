package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.*
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
class ChangeSexAction: Action<Person> {

    override val actionName: String = UserActions.CHANGE_SEX.toString()

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_SEX)
    private lateinit var selectSexText: String

    @StringResource(StringResources.SEX_MALE)
    private lateinit var sexMaleText: String

    @StringResource(StringResources.SEX_FEMALE)
    private lateinit var sexFemaleText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_ENTER_SEX)
    private lateinit var sexChangedMessageText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_PREFERENCES)
    private lateinit var selectPreferenceText: String

    @StringResource(StringResources.PREFERENCE_MALE)
    private lateinit var malePreferenceText: String

    @StringResource(StringResources.PREFERENCE_FEMALE)
    private lateinit var femalePreferenceText: String

    @StringResource(StringResources.PREFERENCE_BOTH)
    private lateinit var bothPreferenceSexText: String

    @Autowired
    private lateinit var personService: PersonService

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            when(person.state){
                PersonEntityState.ACTIVE -> manualChanging(person)
                else -> {
                    when(person.state){
                        PersonEntityState.NOT_CREATED -> message.payload.map{
                            registrationChanging(person, it.args.first().let{Sex.valueOf(it)})
                        }.orElseGet{
                            errorMessage(person.vkId)
                        }
                        else -> message.payload.map{
                            performChanging(person, it.args.first().let{Sex.valueOf(it)})
                        }.orElseGet{
                            manualChanging(person)
                        }
                    }
                }
            }
        }
    }

    private fun manualChanging(person: Person): ResponseMessage{
        return person.copy(state = PersonEntityState.CHANGING_SEX, sex =  Sex.NOT_SELECTED)
                .let(personService::update).map{
                    selectSexMessage(it.vkId)
                }.orElseGet{
                    errorMessage(person.vkId)
                }
    }

    private fun registrationChanging(person: Person, sex: Sex): ResponseMessage{
        return person.copy(sex = sex).let(personService::update).map{
            successRegistration(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private fun performChanging(person: Person, sex: Sex): ResponseMessage{
        return person.copy(state = PersonEntityState.ACTIVE, sex = sex)
                .let(personService::update).map{
            successManualChange(it.vkId)
        }.orElseGet{
            errorMessage(person.vkId)
        }
    }

    private val successRegistration = { vkId: Int ->
        message {
            userVkId = vkId
            text = "$sexChangedMessageText\n$selectPreferenceText"
            keyboard = keyboard {
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

    private val successManualChange = {vkId: Int ->
        message {
            userVkId = vkId
            text     = sexChangedMessageText
            keyboard = toMenuKeyboard
        }
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
            keyboard = toMenuKeyboard
        }
    }

    private val selectSexMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = selectSexText
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