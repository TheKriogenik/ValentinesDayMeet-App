package com.kriogenik.guzeeva.actions

import com.kriogenik.guzeeva.actions.user.UserActions
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.stereotype.Component
import java.util.*
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message

@Component
@ContainsStringResources
class ToMenuAction: Action<Person>{

    override val actionName: String = UserActions.TO_MENU.toString()

    @StringResource(StringResources.USER_MAIN_MENU)
    private lateinit var mainMenuText: String

    @StringResource(StringResources.USER_SEARCH_BUTTON)
    private lateinit var searchButtonText: String

    @StringResource(StringResources.USER_SHOW_BIO_BUTTON)
    private lateinit var showBioButtonText: String

    @StringResource(StringResources.USER_SHOW_HELP_BUTTON)
    private lateinit var showHelpButtonText: String

    @StringResource(StringResources.USER_EXIT_BUTTON)
    private lateinit var exitButtonText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return responseMessage(message.userVkId).let{ Optional.of(it) }
    }

    private val responseMessage = { vkId: Int ->
        message{
            userVkId = vkId
            text = mainMenuText
            keyboard = keyboard {
                row{
                    key{
                        text = searchButtonText
                        payload = UserActions.SEARCH.toString()
                        color   = Key.Color.PRIMARY
                    }
                }
                row{
                    key{
                        text = showBioButtonText
                        payload = UserActions.SHOW_BIO.toString()
                        color   = Key.Color.DEFAULT
                    }
                    key{
                        text = showHelpButtonText
                        payload = UserActions.HELP.toString()
                        color   = Key.Color.DEFAULT
                    }
                }
                row{
                    key{
                        text = exitButtonText
                        payload = UserActions.EXIT.toString()
                        color   = Key.Color.NEGATIVE
                    }
                }
            }
        }
    }
}


