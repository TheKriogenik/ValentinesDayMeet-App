package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.petersamokhin.bots.sdk.objects.Button
import com.petersamokhin.bots.sdk.objects.Color
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import com.petersamokhin.bots.sdk.objects.Keyboard as VkKeyboard

@SpringBootTest
class KeyboardFactoryTest {

    @Autowired
    private lateinit var keyboardFactory: KeyboardFactory<VkKeyboard>

    @MockBean
    private lateinit var buttonFactory: ButtonFactory<Button>

    @Test
    fun createKeyboard(){
        val key = Key("TestMessage", "TestPayload", Key.Color.DEFAULT)
        val button = Button().apply {
            setLabel("TestMessage")
            setPayLoad("TestPayload")
            setColor(Color.Default)
        }
        Mockito.`when`(buttonFactory.createButton(key)).thenReturn(button)
        val keyboard = Keyboard(listOf(listOf(key)))
        val result = keyboardFactory.getKeyboard(keyboard)
        val expected = VkKeyboard().apply {
            addButtons(0, button)
        }
        assert(result.json.toString() == expected.json.toString())
    }

}