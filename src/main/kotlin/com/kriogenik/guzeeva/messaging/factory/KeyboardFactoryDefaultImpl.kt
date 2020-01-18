package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.petersamokhin.bots.sdk.objects.Button
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.petersamokhin.bots.sdk.objects.Keyboard as VkKeyboard

@Component
class KeyboardFactoryDefaultImpl: KeyboardFactory<VkKeyboard> {

    @Autowired
    private lateinit var buttonFactory: ButtonFactory<Button>

    override fun getKeyboard(keyboard: Keyboard): VkKeyboard {
        return VkKeyboard().apply{
            keyboard.rows.mapIndexed{i, keys ->
                keys.map{key ->
                    addButtons(i, buttonFactory.createButton(key))
                }
            }
        }
    }

}
