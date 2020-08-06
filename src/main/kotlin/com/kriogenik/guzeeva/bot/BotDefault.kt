package com.kriogenik.guzeeva.bot

import com.kriogenik.guzeeva.auth.PersonAuthenticator
import com.kriogenik.guzeeva.handlers.received.PersonReceivedMessageHandlerFactory
import com.kriogenik.guzeeva.messaging.factory.ReceivedMessageFactory
import com.kriogenik.guzeeva.messaging.factory.ResponseMessageFactory
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.petersamokhin.bots.sdk.clients.Group
import com.petersamokhin.bots.sdk.objects.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BotDefault : Bot {

    @Autowired
    private lateinit var group: Group

    @Autowired
    private lateinit var receivedMessageFactory: ReceivedMessageFactory<Message>

    @Autowired
    private lateinit var personReceivedMessageHandlerFactory: PersonReceivedMessageHandlerFactory

    @Autowired
    private lateinit var personAuthenticator: PersonAuthenticator

    @Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<Message>

    val errorMessage = { id: Int ->
        ResponseMessage(id, "ERROR", Keyboard.empty(), listOf())
    }

    override fun start() {

        group.onSimpleTextMessage {
            it.let(receivedMessageFactory::createReceivedMessage)
                    .let { receivedMessage ->
                        personAuthenticator.auth(receivedMessage.userVkId)
                                .let(personReceivedMessageHandlerFactory::getMessageHandlerByRole).flatMap { handler ->
                                    handler.handle(receivedMessage)
                                }
                                .map(responseMessageFactory::createResponseMessage)
                                .map {
                                    it.from(group).send()
                                }
                    }
        }
    }


}
