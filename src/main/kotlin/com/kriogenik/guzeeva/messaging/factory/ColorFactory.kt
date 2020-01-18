package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Color

interface ColorFactory {

    fun getColor(color: Key.Color): Color

}
