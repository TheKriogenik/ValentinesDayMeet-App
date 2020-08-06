package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.TableEntityState
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class TablesMenuAction: Action<Person> {

    override val actionName: String = AdminActions.TO_TABLES_MENU.toString()

    @Autowired
    private lateinit var tableService: MeetingTableService

    @StringResource(StringResources.ADMIN_MENU_ADD_TABLE_BUTTON)
    private lateinit var addTableButtonText: String

    @StringResource(StringResources.ADMIN_NO_TABLES)
    private lateinit var noTablesText: String

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toAdminMenuText: String

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return tablesMessage(message.userVkId, tableService.getAll()).let{ Optional.of(it) }
    }

    private final val tablesMessage = {vkId: Int, tables: List<MeetingTable> ->
        when(tables.isEmpty()){
            true -> listOf("Столы отсутствуют")
            else -> tables.map{
                "Стол#${it.id}: " + when(it.state){
                    TableEntityState.FREE    -> "Свободен"
                    TableEntityState.BUSY    -> "Занят"
                    TableEntityState.NOT_USE -> "Не используется"
                }
            }}.foldRight(""){acc, x -> acc + "\n" + x}.let{tablesInfo ->
            log.info(tablesInfo)
            log.info("TEST")
            log.info(addTableButtonText)
                val keyboard = Keyboard(
                        listOf(
                                listOf(
                                        Key(addTableButtonText,
                                                AdminActions.ADD_TABLE.toString(),
                                                Key.Color.DEFAULT)
                                ),
                                listOf(
                                        Key("Освободить стол", AdminActions.FREE_TABLE.toString(), Key.Color.DEFAULT)
                                ),
                                listOf(
                                        Key("Отключить стол", AdminActions.REMOVE_TABLE.toString(), Key.Color.DEFAULT)
                                )
                                ,
                                listOf(
                                        Key("В меню", AdminActions.TO_ADMIN_MENU.toString(), Key.Color.DEFAULT)
                                )
                        )
                )
                ResponseMessage(vkId, tablesInfo, keyboard, listOf())
            }
        }

    private val noTablesMessage = { vkId: Int ->
        message {
            userVkId = vkId
            text     = noTablesText
            keyboard = keyboard {
                row {
                    key {
                        text    = toAdminMenuText
                        payload = AdminActions.TO_ADMIN_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
