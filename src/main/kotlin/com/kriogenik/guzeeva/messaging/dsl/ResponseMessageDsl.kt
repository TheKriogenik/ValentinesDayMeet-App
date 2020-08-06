package com.kriogenik.guzeeva.messaging.dsl

import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import java.util.*

class ResponseMessageDsl private constructor() {

    var userVkId: Int = 0

    var text: String  = "Empty text"

    var keyboard: Keyboard = Keyboard.empty()

    var attachments: List<String> = Collections.emptyList()

    fun build(): ResponseMessage{
        return ResponseMessage(userVkId, text, keyboard, attachments)
    }

    companion object{

        fun message(block: ResponseMessageDsl.() -> Unit) = ResponseMessageDsl().apply(block).build()

    }

}
