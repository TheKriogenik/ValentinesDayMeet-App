package com.kriogenik.guzeeva.messaging.model

data class Key(
        val message: String,
        val payload: String,
        val color:   Color
){

    enum class Color{
        DEFAULT,
        PRIMARY,
        NEGATIVE,
        POSITIVE
    }

}
