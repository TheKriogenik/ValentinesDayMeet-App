package com.kriogenik.guzeeva.handlers.received

import com.kriogenik.guzeeva.model.PersonRole
import java.util.*

interface PersonReceivedMessageHandlerFactory {

    fun getMessageHandlerByRole(role: PersonRole.Role): Optional<PersonReceivedMessageHandler>

}
