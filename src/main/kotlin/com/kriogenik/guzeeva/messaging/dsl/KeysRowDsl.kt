package com.kriogenik.guzeeva.messaging.dsl

import com.kriogenik.guzeeva.messaging.model.Key

class KeysRowDsl {

    private val keys: MutableList<Key> = mutableListOf()

    private fun build(): List<Key>{
        return keys.toList()
    }

    fun key(block: KeyDsl.KeyBuilder.() -> Unit){
        KeyDsl.key(block).let(keys::add)
    }

    companion object{

        fun row(block: KeysRowDsl.() -> Unit) = KeysRowDsl().apply(block).build()

    }

}
