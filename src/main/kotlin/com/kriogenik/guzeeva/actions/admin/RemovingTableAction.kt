package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.*
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.TableEntityState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class RemovingTableAction: Action<Person> {

    override val actionName: String = AdminActions.REMOVE_TABLE.toString()

    @Autowired
    private lateinit var tableService: MeetingTableService

    @Autowired
    private lateinit var personService: PersonService

    private final val log = LoggerFactory.getLogger(this::class.java)

    private final val disableTable = "Отключить стол"

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $message")
        return message.message.let{number ->
            when(number == disableTable){
                true -> codeNotEntered(message.userVkId).orElseGet{
                    errorMessage(message.userVkId)
                }
                else -> codeEntered(message.userVkId, number)
            }
        }.let { Optional.of(it) }
    }

    private fun codeEntered(vkId: Int, arg: String): ResponseMessage {
        return personService.find(vkId).flatMap{person ->
            person.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{
                tableService.find(arg.toInt()).map{table ->
                    log.info("TARGET TABLE: $table")
                    when(table.state){
                        TableEntityState.FREE -> tableService.delete(table).let{
                            ResponseMessage(vkId, "Стол отключен", Keyboard(
                                    listOf(
                                            listOf(
                                                    Key("В меню", AdminActions.TO_ADMIN_MENU.toString(), Key.Color.POSITIVE)
                                            )
                                    )
                            ), listOf())
                        }
                        else -> errorTableRemovingMessage(vkId)
                    }
                }
            }
        }.orElseGet{
            errorMessage(vkId)
        }
    }

    private val errorTableRemovingMessage = {vkId: Int ->
        ResponseMessage(vkId, "Стол не удалось удалить", Keyboard(
                listOf(
                        listOf(
                                Key("В меню", AdminActions.TO_ADMIN_MENU.toString(), Key.Color.NEGATIVE)
                        )
                )
        ), listOf())
    }

    private val errorMessage = {vkId: Int ->
        ResponseMessage(vkId, "Ошибка", Keyboard(
                listOf(
                        listOf(
                                Key("В меню", AdminActions.TO_ADMIN_MENU.toString(), Key.Color.NEGATIVE)
                        )
                )
        ), listOf())
    }

    private fun codeNotEntered(vkId: Int): Optional<ResponseMessage>{
        return personService.find(vkId).flatMap{
            it.copy(state = PersonEntityState.ENTERING_CLOSE_TABLE_CODE).let(personService::update).map{
                ResponseMessage(vkId, "Введите код стола", Keyboard(
                        listOf(
                                listOf(
                                        Key("В меню", AdminActions.TO_ADMIN_MENU.toString(), Key.Color.DEFAULT)
                                )
                        )
                ), listOf())
            }
        }
    }

}
