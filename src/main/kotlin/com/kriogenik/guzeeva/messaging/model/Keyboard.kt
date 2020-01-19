package com.kriogenik.guzeeva.messaging.model

import java.util.*

data class Keyboard(
        val rows: List<List<Key>>
){
    companion object{
        fun empty() = Keyboard(Collections.emptyList())
    }
}
