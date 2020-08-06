package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.TableEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class AddTableAction: Action<Person> {

    override val actionName: String = AdminActions.ADD_TABLE.toString()

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var tableService: MeetingTableService

    @StringResource(StringResources.ERROR_MESSAGE)
    private lateinit var errorMessageText: String

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ADMIN_MENU_ADD_TABLE_BUTTON)
    private lateinit var addTableButtonText: String

    @StringResource(StringResources.ADMIN_ENTER_TABLE_CODE)
    private lateinit var enterTableCodeText: String

    @StringResource(StringResources.ADMIN_ERROR_TABLE_ALREADY_EXIST)
    private lateinit var tableAlreadyExistErrorText: String

    @StringResource(StringResources.ADMIN_SUCCESS_TABLE_ADDED)
    private lateinit var tableSuccessAddedText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return message.message.let{inputText ->
            log.info("$inputText == $addTableButtonText")
            (inputText).toList().zip(addTableButtonText.toList()).map{(a, b) ->
                "$a:${a.toInt()} == $b:${b.toInt()}"
            }.reduce{acc, s ->
                acc + "\n" + s
            }.let(::println)
            when(inputText == addTableButtonText){
                true -> {
                    log.info("Code not entered line")
                    codeNotEntered(message.userVkId)
                }
                else -> {
                    log.info("Code entered line")
                    inputText.toIntOrNull().let{Optional.ofNullable(it)}.map{
                        codeEntered(message.userVkId, it)
                    }
                }
            }
        }
    }

    private fun codeEntered(vkId: Int, tableId: Int): ResponseMessage{
        return personService.find(vkId).flatMap{person ->
            person.copy(state = PersonEntityState.ACTIVE_ADMIN).let(personService::update).map{
                tableService.find(tableId).flatMap{
                    tableAlreadyExistErrorMessage(vkId).let{ Optional.of(it) }
                }.orElseGet{
                    MeetingTable(id = tableId, state = TableEntityState.FREE).let(tableService::create).map{
                        tableAddSuccessMessage(vkId)
                    }.orElseGet{
                        errorMessage(vkId)
                    }
                }
            }
        }.orElseGet{
            errorMessage(vkId)
        }
    }

    private fun codeNotEntered(vkId: Int): Optional<ResponseMessage>{
        return personService.find(vkId).flatMap{
            it.copy(state = PersonEntityState.ENTERING_NEW_TABLE_CODE).let(personService::update).map{person ->
                codeNotEnteredMessage(person.vkId)
            }
        }
    }

    private val tableAddSuccessMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = tableSuccessAddedText
            keyboard = toMenuKeyboard
        }
    }

    private val codeNotEnteredMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = enterTableCodeText
            keyboard = toMenuKeyboard
        }
    }

    private val tableAlreadyExistErrorMessage = {vkId: Int ->
        message{
            userVkId = vkId
            text     = tableAlreadyExistErrorText
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

    private val toMenuKeyboard by lazy {
        keyboard {
            row{
                key{
                    text    = toMenuButtonText
                    payload = AdminActions.TO_ADMIN_MENU.toString()
                    color   = Key.Color.NEGATIVE
                }
            }

        }
    }

}
