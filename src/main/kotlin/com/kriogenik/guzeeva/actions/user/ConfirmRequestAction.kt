package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.TableEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import com.petersamokhin.bots.sdk.clients.Group
import com.petersamokhin.bots.sdk.objects.Message
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ConfirmRequestAction: Action<Person> {

    override val actionName: String = UserActions.CONFIRM_REQUEST.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var tableService: MeetingTableService

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    @StringResource(StringResources.ANSWER_REQUEST_CONFIRM_TARGET)
    private lateinit var requestSuccessTargetText: String

    @StringResource(StringResources.ANSWER_REQUEST_CONFIRM_INITIAL)
    private lateinit var requestSuccessInitialText: String

    @StringResource(StringResources.USER_STOP_TALKING_BUTTON)
    private lateinit var stopTalkingButtonText: String

    @StringResource(StringResources.ANSWER_NO_FREE_TABLES)
    private lateinit var noFreeTablesText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorText: String

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $message")
        return personService.find(message.userVkId).flatMap{targetPerson ->
            log.info("TARGET PERSON: $targetPerson")
            message.payload.flatMap{
                it.args.firstOrNull().let{ Optional.ofNullable(it?.toIntOrNull()) }.flatMap{ initialId ->
                    log.info("INITIAL ID: $initialId")
                    personService.find(initialId).map{initialPerson ->
                        log.info("INITIAL PERSON: $it")
                        confirmRequest(initialPerson, targetPerson)
                    }
                }
            }
        }
    }

    private fun confirmRequest(initialPerson: Person, targetPerson: Person): ResponseMessage{
        log.info("CONFIRM REQUEST")
        return getFreeTable().flatMap {
            initialPerson.copy(state = PersonEntityState.TALKING)
                    .let(personService::update).flatMap{updatedInitialPerson ->
                targetPerson.copy(state = PersonEntityState.TALKING)
                        .let(personService::update).flatMap{updatedTargetPerson ->
                    it.copy(person1 = updatedInitialPerson, person2 = updatedTargetPerson, state = TableEntityState.BUSY)
                            .let(tableService::update).map{ table ->
                        responseMessageFactory.createResponseMessage(confirmMessageInitial(initialPerson.vkId, table.id))
                                .from(group).send()
                        confirmMessageTarget(targetPerson.vkId, table.id)
                    }
                }
            }
        }.orElseGet{
            rollBack(initialPerson, targetPerson)
        }
    }

    private fun rollBack(initialPerson: Person, targetPerson: Person): ResponseMessage{
        return initialPerson.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{
            targetPerson.copy(state = PersonEntityState.ACTIVE).let(personService::update).map{
                responseMessageFactory.createResponseMessage(noFreeTables(initialPerson.vkId)).from(group).send()
                noFreeTables(targetPerson.vkId)
            }
        }.orElseGet{
            strangeError(targetPerson.vkId)
        }
    }

    private fun getFreeTable(): Optional<MeetingTable>{
        return tableService.getFreeTables().firstOrNull().let{
            Optional.ofNullable(it)
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

    private val noFreeTables = {vkId: Int ->
        message {
            userVkId = vkId
            text     = noFreeTablesText
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

    private val confirmMessageTarget = {vkId: Int, tableNum: Int ->
        message {
            userVkId = vkId
            text     = requestSuccessTargetText + tableNum
            keyboard = keyboard {
                row {
                    key {
                        text    = stopTalkingButtonText
                        payload = "${UserActions.STOP_TALKING}:$tableNum"
                        color   = Key.Color.NEGATIVE
                    }
                }
            }
        }
    }

    private val confirmMessageInitial = {vkId: Int, tableNum: Int ->
        message {
            userVkId = vkId
            text     = requestSuccessInitialText + tableNum
            keyboard = keyboard {
                row {
                    key {
                        text    = stopTalkingButtonText
                        payload = "${UserActions.STOP_TALKING}:$tableNum"
                        color   = Key.Color.NEGATIVE
                    }
                }
            }
        }
    }

}
