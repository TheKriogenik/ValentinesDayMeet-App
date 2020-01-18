package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.ResponseMessage

interface ResponseMessageFactory<VK_MESSAGE> {

    fun createResponseMessage(responseMessage: ResponseMessage): VK_MESSAGE

}
