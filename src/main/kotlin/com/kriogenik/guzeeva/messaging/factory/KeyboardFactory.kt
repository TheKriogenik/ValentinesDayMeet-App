package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Keyboard

interface KeyboardFactory<KEYBOARD> {

    fun getKeyboard(keyboard: Keyboard): KEYBOARD

}
