package com.kriogenik.guzeeva.bot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BotInitializer {

    @Autowired
    private lateinit var bot: Bot

    @PostConstruct
    fun init(){
        bot.start()
    }

}
