package com.kriogenik.guzeeva.actions.notauthenticated

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.bot.VkLoader
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.Sex
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ShowRegistrationMessageAction: Action<Person> {

    override val actionName: String = NotAuthenticatedPersonActions.SHOW_REGISTRATION.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var sexLoader: VkLoader<Sex>

    @StringResource(StringResources.REGISTRATION_HELLO_MESSAGE)
    private lateinit var helloMessage: String

    @StringResource(StringResources.PHOTO_REGISTRATION_MESSAGE)
    private lateinit var photo: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return sexLoader.load(message.userVkId)
                .orElse(Sex.NOT_SELECTED).let{sex ->
                    Person(vkId = message.userVkId,
                            sex = sex,
                            state = PersonEntityState.ENTERING_REGISTRATION_CODE)
                            .let(personService::create).map{person ->
                                    registrationMessage(person.vkId)
                            }
                }
    }

    private val registrationMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = helloMessage
            attachments = listOf(photo)
        }
    }

}
