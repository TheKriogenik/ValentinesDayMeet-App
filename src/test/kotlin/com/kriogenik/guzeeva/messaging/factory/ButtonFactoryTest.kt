package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.petersamokhin.bots.sdk.objects.Button
import com.petersamokhin.bots.sdk.objects.Color
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class ButtonFactoryTest {

    @Autowired
    private lateinit var buttonFactory: ButtonFactory<Button>

    @MockBean
    private lateinit var colorFactory: ColorFactory<Color>

    @Test
    fun createButtonExample(){
        val keys = (0..100).map{
            Key("Message$it", "Payload$it", Key.Color.values()[it % Key.Color.values().size])
        }
        val buttons = (0..100).map{
            Button().apply{
                setLabel("Message$it")
                setPayLoad("Payload$it")
                setColor(Color.Default)
            }
        }
        Mockito.`when`(colorFactory.getColor(Key.Color.DEFAULT))
                .thenReturn(Color.Default)
        Mockito.`when`(colorFactory.getColor(Key.Color.NEGATIVE))
                .thenReturn(Color.Default)
        Mockito.`when`(colorFactory.getColor(Key.Color.POSITIVE))
                .thenReturn(Color.Default)
        Mockito.`when`(colorFactory.getColor(Key.Color.PRIMARY))
                .thenReturn(Color.Default)
        assert(keys.map(buttonFactory::createButton).zip(buttons)
                .map{(result, expected) ->
                    result.json.toString() == expected.json.toString()
                }.all{it})
    }

}
