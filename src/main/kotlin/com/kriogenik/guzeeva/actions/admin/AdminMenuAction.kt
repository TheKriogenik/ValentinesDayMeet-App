package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class AdminMenuAction: Action<Person> {

    override val actionName: String = AdminActions.TO_ADMIN_MENU.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.ADMIN_TO_TABLES_MENU_BUTTON)
    private lateinit var toTablesButtonText: String

    @StringResource(StringResources.ADMIN_GET_KEY_BUTTON)
    private lateinit var getKeyButtonText: String

    @StringResource(StringResources.ADMIN_MAIN_MENU)
    private lateinit var mainMenuText: String

    @StringResource(StringResources.PHOTO_MAIN_MENU_ADMIN)
    private lateinit var photo: String

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $message")
        return personService.find(message.userVkId).flatMap{
            it.copy(state = PersonEntityState.ACTIVE).let(personService::update).flatMap{
                responseMessage(message.userVkId).let{ Optional.of(it) }
            }
        }
    }

    val responseMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = mainMenuText
            keyboard = keyboard {
                row {
                    key {
                        text    = toTablesButtonText
                        payload = AdminActions.TO_TABLES_MENU.toString()
                        color   = Key.Color.PRIMARY
                    }
                }
                row {
                    key {
                        text    = getKeyButtonText
                        payload = AdminActions.GET_NEW_USER_CODE.toString()
                        color   = Key.Color.DEFAULT
                    }
                }
            }
            attachments = listOf(photo)
        }
    }

}
