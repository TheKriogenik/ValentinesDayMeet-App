package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class GetHelpAction: Action<Person> {

    override val actionName: String = UserActions.HELP.toString()

    @StringResource(StringResources.USER_HELP)
    private lateinit var helpText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.PHOTO_HELP)
    private lateinit var photo: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return message {
            userVkId = message.userVkId
            text     = helpText
            keyboard = keyboard {
                row {
                    key {
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
            attachments = listOf(photo)
        }.let { Optional.of(it) }
    }

}
