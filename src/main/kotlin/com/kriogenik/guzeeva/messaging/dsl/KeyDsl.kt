package com.kriogenik.guzeeva.messaging.dsl

import com.kriogenik.guzeeva.messaging.model.Key

class KeyDsl {

    class KeyBuilder{
        var text: String = ""
        var payload: String = ""
        var color: Key.Color = Key.Color.DEFAULT

        fun build(): Key = Key(text, payload, color)

    }

    companion object{
        fun key(block: KeyBuilder.() -> Unit) = KeyBuilder().apply(block).build()
    }

}