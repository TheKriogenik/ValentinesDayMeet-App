package com.kriogenik.guzeeva.messaging.model

data class ResponseMessage(
        val message:     String,
        val keyboard: Keyboard,
        val attachments: List<String>)
