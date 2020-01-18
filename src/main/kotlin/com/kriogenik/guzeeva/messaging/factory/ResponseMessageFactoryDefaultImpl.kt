package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.petersamokhin.bots.sdk.objects.Message as VkMessage
import com.petersamokhin.bots.sdk.objects.Keyboard as VkKeyboard

@Component
class ResponseMessageFactoryDefaultImpl: ResponseMessageFactory<VkMessage> {

    @Autowired
    private lateinit var keyboardFactory: KeyboardFactory<VkKeyboard>

    override fun createResponseMessage(responseMessage: ResponseMessage): VkMessage {
        return VkMessage().apply {
            text(responseMessage.message)
            this.to(responseMessage.userVkId)
            when(responseMessage.attachments.isEmpty()){
                true -> {}
                else -> responseMessage.attachments.foldRight(""){acc, x -> "$acc.$x" }.let{
                    this.attachments(it)
                }
            }
            when(responseMessage.keyboard.rows.isEmpty()){
                true -> {}
                else -> this.keyboard(responseMessage.keyboard.let(keyboardFactory::getKeyboard))
            }

        }
    }

}
