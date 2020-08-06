package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.MeetingTableService
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
class StopTalkingAction: Action<Person> {

    override val actionName: String = UserActions.STOP_TALKING.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var tableService: MeetingTableService

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.USER_STOP_TALKING_TARGET)
    private lateinit var talkingStoppedTargetText: String

    @StringResource(StringResources.USER_STOP_TALKING_INITIAL)
    private lateinit var talkingStoppedInitialText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return message.payload.map{payload ->
            payload.args.firstOrNull().let{ Optional.ofNullable(it?.toIntOrNull()) }.map{tableNum ->
                stopTalking(message.userVkId, tableNum)
            }.orElseGet{
                errorMessage(message.userVkId)
            }
        }
    }

    private fun stopTalking(initialPersonId: Int, tableNum: Int): ResponseMessage{
        return tableService.find(tableNum).flatMap{
            val (person1, person2) = (it.person1 to it.person2)
            when(person1 != null && person2 != null){
                true -> {
                    personService.find(person1.vkId).flatMap{
                        it.copy(state = PersonEntityState.ACTIVE)
                                .let(personService::update).flatMap{person1Updated ->
                                    personService.find(person2.vkId).flatMap{
                                        it.copy(state = PersonEntityState.ACTIVE)
                                                .let(personService::update).map{person2Updated ->
                                                    val targetPersonId = when(initialPersonId){
                                                        person1Updated.vkId -> person2Updated.vkId
                                                        else                -> person1Updated.vkId
                                                    }
                                                    stopTalkingTargetMessage(targetPersonId)
                                                            .let(responseMessageFactory::createResponseMessage)
                                                            .from(group)
                                                            .send()
                                                    stopTalkingInitialMessage(initialPersonId)
                                                }
                                    }
                                }
                    }
                }
                else -> { errorMessage(initialPersonId).let{ Optional.of(it) } }
            }
        }.orElseGet{
            errorMessage(initialPersonId)
        }
    }

    private val stopTalkingInitialMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = talkingStoppedInitialText
            keyboard = keyboard {
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

    private val stopTalkingTargetMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = talkingStoppedTargetText
            keyboard = keyboard {
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

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text = errorText
            keyboard = keyboard {
                row {
                    key {
                        text = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
