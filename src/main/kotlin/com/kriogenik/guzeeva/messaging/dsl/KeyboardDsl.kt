package com.kriogenik.guzeeva.messaging.dsl

import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard

class KeyboardDsl {

    class KeyboardBuilder{
        private var rows: MutableList<List<Key>> = mutableListOf()

        fun row(block: KeysRowDsl.() -> Unit){
            KeysRowDsl.row(block).let(rows::add)
        }

        fun build(): Keyboard{
            return Keyboard(rows.toList())
        }

    }

    companion object{

        fun keyboard(block: KeyboardBuilder.() -> Unit) = KeyboardBuilder().apply(block).build()

    }

}
