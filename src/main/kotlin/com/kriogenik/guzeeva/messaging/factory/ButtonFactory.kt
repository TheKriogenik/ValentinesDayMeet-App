package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Button

interface ButtonFactory<BUTTON> {

    fun createButton(key: Key): BUTTON

}
