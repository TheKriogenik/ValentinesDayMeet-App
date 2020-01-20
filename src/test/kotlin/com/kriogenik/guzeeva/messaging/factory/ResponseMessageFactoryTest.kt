package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import com.petersamokhin.bots.sdk.objects.Message as VkMessage
import com.petersamokhin.bots.sdk.objects.Keyboard as VkKeyboard

@SpringBootTest
class ResponseMessageFactoryTest {

    /*@Autowired
    private lateinit var responseMessageFactory: ResponseMessageFactory<VkMessage>

    @MockBean
    private lateinit var keyboardFactory: KeyboardFactory<VkKeyboard>

    @Test
    fun generateResponseMessage(){
        val keyboard = Keyboard(Collections.emptyList())
        val responseMessage = ResponseMessage(1, "TEST_MESSAGE",
                keyboard,
                listOf())
        val expected = VkMessage().apply{
            to(1)
            text("TEST_MESSAGE")
            keyboard(VkKeyboard())
            attachments()
        }
        Mockito.`when`(keyboardFactory.getKeyboard(keyboard)).thenReturn(VkKeyboard())
        val actual = responseMessageFactory.createResponseMessage(responseMessage)
        assert(expected == actual)
    }
*/
}
