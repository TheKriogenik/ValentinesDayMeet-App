package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.petersamokhin.bots.sdk.clients.User
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import com.petersamokhin.bots.sdk.objects.Message as VkMessage

@SpringBootTest
class ReceivedMessageFactoryTest {

    @Autowired
    private lateinit var receivedMessageFactory: ReceivedMessageFactory<VkMessage>

    @Mock
    private lateinit var vkMessage: VkMessage

    @Test
    fun createReceivedMessage(){
        Mockito.`when`(vkMessage.attachmentsOfReceivedMessage).thenReturn(JSONObject().apply{
            put("payload", "PAYLOAD")
        })
        Mockito.`when`(vkMessage.text).thenReturn("TEST_TEST")
        Mockito.`when`(vkMessage.authorId()).thenReturn(1)
        val expected = ReceivedMessage(1,"TEST_TEST", "PAYLOAD")
        val actual = receivedMessageFactory.createReceivedMessage(vkMessage)
        assert(expected == actual)
    }

}