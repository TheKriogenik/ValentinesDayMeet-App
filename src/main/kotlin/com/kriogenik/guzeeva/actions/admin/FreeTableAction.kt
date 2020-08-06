package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.actions.user.UserActions
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
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
class FreeTableAction: Action<Person> {

    override val actionName: String = AdminActions.FREE_TABLE.toString()

    private final val log = LoggerFactory.getLogger(this::class.java)

    @StringResource(StringResources.ADMIN_MENU_FREE_TABLE_BUTTON)
    private lateinit var freeTableButtonText: String

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toAdminMenuButtonText: String

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.ADMIN_SUCCESS_TABLE_FREED)
    private lateinit var tableFreedText: String

    @StringResource(StringResources.TABLE_TIME_IS_OVER)
    private lateinit var timeOverText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toUserMenuButtonText: String

    @StringResource(StringResources.ADMIN_ENTER_TABLE_CODE)
    private lateinit var enterTableCodeText: String

    @StringResource(StringResources.ADMIN_ERROR_TABLE_REMOVING)
    private lateinit var errorTableRemovingText: String

    @StringResource(StringResources.ERROR_TABLE_ALREADY_FREED)
    private lateinit var errorTableAlreadyFreedText: String

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var tableService: MeetingTableService

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $message")
        return message.message.let{number ->
            log.info("Message.message = ${message.message} == $freeTableButtonText : ${freeTableButtonText == message.message}")
            when(number == freeTableButtonText){
                true -> {
                    log.info("CODE NOT ENTERED")
                    codeNotEntered(message.userVkId).orElseGet{
                        errorMessage(message.userVkId)
                    }
                }
                else -> {
                    log.info("CODE ENTERED")
                    codeEntered(message.userVkId, number)
                }
            }
        }.let{ Optional.of(it) }
    }

    private fun codeEntered(vkId: Int, arg: String): ResponseMessage {
        return personService.find(vkId).flatMap{person ->
            person.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{
                tableService.find(arg.toInt()).map{table ->
                    log.info("TARGET TABLE: $table")
                    when(table.state){
                        TableEntityState.FREE -> errorTableAlreadyFreedMessage(vkId)
                        TableEntityState.BUSY -> freeTable(vkId ,table)
                        else -> errorTableRemovingMessage(vkId)
                    }
                }
            }
        }.orElseGet{
            errorMessage(vkId)
        }
    }

    private fun freeTable(vkId: Int, table: MeetingTable): ResponseMessage{
        return when(table.person1 != null && table.person2 != null){
            true -> {
                table.person1.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{person1 ->
                    table.person2.copy(state = PersonEntityState.ACTIVE).let(personService::update).map{person2 ->
                        listOf(audienceOverMessage(person1.vkId), audienceOverMessage(person2.vkId))
                                .map(responseMessageFactory::createResponseMessage)
                                .forEach{it.from(group).send()}
                        successMessage(vkId)
                    }
                }.orElseGet{
                    freeTableErrorMessage(vkId)
                }
            }
            else -> freeTableErrorMessage(vkId)
        }
    }

    private val freeTableErrorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
            keyboard = toAdminMenuKeyboard
        }
    }

    private val toAdminMenuKeyboard by lazy {
        keyboard {
            row {
                key {
                    text    = toAdminMenuButtonText
                    payload = AdminActions.TO_ADMIN_MENU.toString()
                    color   = Key.Color.POSITIVE
                }
            }
        }
    }

    private val successMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = tableFreedText
            keyboard = toAdminMenuKeyboard
        }
    }

    private val audienceOverMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text      = timeOverText
            keyboard  = keyboard{
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

    private val errorTableRemovingMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorTableRemovingText
            keyboard = toAdminMenuKeyboard
        }
    }

    private val errorTableAlreadyFreedMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = errorTableAlreadyFreedText
            keyboard = toAdminMenuKeyboard
        }
    }

    private fun codeNotEntered(vkId: Int): Optional<ResponseMessage>{
        return personService.find(vkId).flatMap{
            it.copy(state = PersonEntityState.ENTERING_FREE_TABLE_CODE).let(personService::update).map{
                enterTableCodeMessage(it.vkId)
            }
        }
    }

    private val errorMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = errorMessageText
            keyboard = toAdminMenuKeyboard
        }
    }

    private val enterTableCodeMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = enterTableCodeText
            keyboard = toAdminMenuKeyboard
        }
    }

}
