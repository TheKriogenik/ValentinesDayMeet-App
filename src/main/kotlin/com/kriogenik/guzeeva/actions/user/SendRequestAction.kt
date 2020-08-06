package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
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
import com.petersamokhin.bots.sdk.clients.Group
import com.petersamokhin.bots.sdk.objects.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class SendRequestAction: Action<Person> {

    override val actionName: String = UserActions.SEND_REQUEST.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.PROFILE_NAME)
    private lateinit var profileNameText: String

    @StringResource(StringResources.PROFILE_AGE)
    private lateinit var profileAgeText: String

    @StringResource(StringResources.PROFILE_SEX)
    private lateinit var profileSexText: String

    @StringResource(StringResources.PROFILE_PREFERENCE)
    private lateinit var profilePreferenceText: String

    @StringResource(StringResources.PROFILE_BIO)
    private lateinit var profileBioText: String

    @StringResource(StringResources.SEX_MALE)
    private lateinit var maleSex: String

    @StringResource(StringResources.SEX_MALE)
    private lateinit var femaleSex: String

    @StringResource(StringResources.SEX_NOT_SELECTED)
    private lateinit var notSelectedSex: String

    @StringResource(StringResources.PREFERENCE_MALE)
    private lateinit var malePreference: String

    @StringResource(StringResources.PREFERENCE_FEMALE)
    private lateinit var femalePreference: String

    @StringResource(StringResources.PREFERENCE_BOTH)
    private lateinit var bothPreference: String

    @StringResource(StringResources.PREFERENCE_NOT_SELECTED)
    private lateinit var notSelectedPreference: String

    @StringResource(StringResources.NEW_REQUEST_TITLE)
    private lateinit var newRequestTitleText: String

    @StringResource(StringResources.NEW_REQUEST_ACCEPT_BUTTON)
    private lateinit var requestConfirmButtonText: String

    @StringResource(StringResources.NEW_REQUEST_DECLINE_BUTTON)
    private lateinit var requestDeclineButtonText: String

    @StringResource(StringResources.USER_SEARCH_NEXT_BUTTON)
    private lateinit var searchNextButtonText: String

    @StringResource(StringResources.USER_REQUEST_SEND_SUCCESS)
    private lateinit var requestSendText: String

    @StringResource(StringResources.USER_CANCEL_REQUEST)
    private lateinit var cancelRequestButtonText: String

    @StringResource(StringResources.USER_WAITING_RESPONSE)
    private lateinit var waitingResponseText: String

    @StringResource(StringResources.USER_ANSWERING_REQUEST)
    private lateinit var answeringRequestText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).flatMap{person ->
            message.payload.flatMap{payload ->
                payload.args.firstOrNull().let{ Optional.ofNullable(it) }
            }.flatMap{targetId ->
                personService.find(targetId.toInt()).map{targetPerson ->
                    when(targetPerson.state){
                        PersonEntityState.ACTIVE           -> sendRequest(person, targetPerson)
                        PersonEntityState.ANSWER_REQUEST   -> answerRequestMessage(person.vkId, targetPerson.vkId)
                        PersonEntityState.WAITING_RESPONSE -> waitingResponseMessage(person.vkId, targetPerson.vkId)
                        else                               -> strangeError(person.vkId)
                    }
                }
            }
        }
    }

    private fun sendRequest(initialPerson: Person, targetPerson: Person): ResponseMessage{
        return initialPerson.copy(state = PersonEntityState.WAITING_RESPONSE).let(personService::update).flatMap{init ->
            targetPerson.copy(state = PersonEntityState.ANSWER_REQUEST).let(personService::update).map{target ->
                responseMessageFactory.createResponseMessage(newRequestMessage(init, target.vkId)).from(group).send()
                requestSendMessage(initialPerson.vkId, target.vkId)
            }
        }.orElseGet {
            strangeError(initialPerson.vkId)
        }
    }

    private final val requestSendMessage = {vkId: Int, targetId: Int ->
        message {
            userVkId = vkId
            text     = requestSendText
            keyboard = keyboard {
                row {
                    key {
                        text    = cancelRequestButtonText
                        payload = "${UserActions.CANCEL_REQUEST}:$targetId"
                        color   = Key.Color.NEGATIVE
                    }
                }
            }
        }
    }

    private final val waitingResponseMessage = {vkId: Int, prevId: Int ->
        message {
            userVkId = vkId
            text     = waitingResponseText
            keyboard = keyboard {
                row {
                    key {
                        text    = searchNextButtonText
                        payload = "${UserActions.SEARCH}:$prevId"
                        color   = Key.Color.POSITIVE
                    }
                    key {
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.PRIMARY
                    }
                }
            }
        }
    }

    private final val newRequestMessage = {initialPerson: Person, targetId: Int ->
        val bio =  """$newRequestTitleText
        |$profileNameText: ${initialPerson.name}
        |$profileAgeText: ${initialPerson.age}
        |$profileSexText: ${showSex(initialPerson.sex)}
        |$profilePreferenceText: ${showPreference(initialPerson.preference)}
        |$profileBioText: ${initialPerson.bio}
    """.trimMargin()
        message {
            userVkId = targetId
            text     = bio
            keyboard = keyboard {
                row {
                    key {
                        text    = requestConfirmButtonText
                        payload = "${UserActions.CONFIRM_REQUEST}:${initialPerson.vkId}"
                        color   = Key.Color.POSITIVE
                    }
                    key {
                        text    = requestDeclineButtonText
                        payload = "${UserActions.DECLINE_REQUEST}:${initialPerson.vkId}"
                        color   = Key.Color.NEGATIVE
                    }
                }
            }
        }
    }

    private fun showSex(sex: Sex): String = when(sex){
        Sex.FEMALE       -> femaleSex
        Sex.MALE         -> maleSex
        Sex.NOT_SELECTED -> notSelectedSex
    }

    private fun showPreference(preference: Preference): String = when(preference){
        Preference.MALE         -> malePreference
        Preference.FEMALE       -> femalePreference
        Preference.BOTH         -> bothPreference
        Preference.NOT_SELECTED -> notSelectedPreference
    }

    private final val answerRequestMessage = {vkId: Int, prevId: Int ->
        message {
            userVkId = vkId
            text     = answeringRequestText
            keyboard = keyboard {
                row {
                    key {
                        text    = searchNextButtonText
                        payload = "${UserActions.SEARCH}:$prevId"
                        color   = Key.Color.POSITIVE
                    }
                    key {
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.PRIMARY
                    }
                }
            }
        }
    }

    private final val strangeError = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
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
