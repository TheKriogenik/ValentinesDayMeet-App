package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Color
import org.springframework.stereotype.Component

@Component
class ColorFactoryDefaultImpl: ColorFactory<Color> {

    override fun getColor(color: Key.Color): Color {
        return when(color){
            Key.Color.DEFAULT  -> Color.Default
            Key.Color.PRIMARY  -> Color.Primary
            Key.Color.NEGATIVE -> Color.Negative
            Key.Color.POSITIVE -> Color.Positive
        }
    }

}
