package com.kriogenik.guzeeva.messaging.model

data class ResponseMessage(
        val userVkId:    Int,
        val message:     String,
        val keyboard:    Keyboard,
        val attachments: List<String>)
