package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Button
import com.petersamokhin.bots.sdk.objects.Color
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ButtonFactoryDefaultImpl: ButtonFactory<Button> {

    @Autowired
    private lateinit var colorFactory: ColorFactory<Color>

    override fun createButton(key: Key): Button {
        return Button().apply{
            setLabel(key.message)
            setPayLoad(key.payload)
            setColor(colorFactory.getColor(key.color))
        }
    }

}
