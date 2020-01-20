package com.kriogenik.guzeeva.messaging.model

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.actions.user.UserActions
import com.kriogenik.guzeeva.model.Person

data class Payload(
        val action: Action<Person>,
        val args:   List<String>)
