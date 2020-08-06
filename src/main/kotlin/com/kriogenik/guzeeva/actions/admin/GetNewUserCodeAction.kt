package com.kriogenik.guzeeva.actions.admin

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.RegistrationCodeService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class GetNewUserCodeAction: Action<Person> {

    override val actionName: String = AdminActions.GET_NEW_USER_CODE.toString()

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.ADMIN_NEW_USER_CODE)
    private lateinit var newCodeMessageText: String

    @Autowired
    private lateinit var registrationCodeService: RegistrationCodeService

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return registrationCodeService.getNewCode().let { code ->
            responseMessage(message.userVkId, code.code)
        }.let { Optional.of(it) }
    }

    private var responseMessage = { vkId: Int, code: String ->
        message {
            userVkId = vkId
            text     = "$newCodeMessageText: $code"
            keyboard = keyboard {
                row {
                    key {
                        text    = toMenuButtonText
                        payload = AdminActions.TO_ADMIN_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

}
