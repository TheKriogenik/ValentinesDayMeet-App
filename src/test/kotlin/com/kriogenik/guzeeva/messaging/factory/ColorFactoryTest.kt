package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Color
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ColorFactoryTest {

    @Autowired
    private lateinit var colorFactory: ColorFactory

    @Test
    fun allColorsAvailable(){
        Key.Color.values().map{keyColor ->
            colorFactory.getColor(keyColor)
        }.all{color ->
            Color.values().contains(color)
        }.let(::assert)
    }
    
}
