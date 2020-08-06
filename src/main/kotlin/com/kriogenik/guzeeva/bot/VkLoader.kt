package com.kriogenik.guzeeva.bot

import java.util.*

interface VkLoader<T> {


    fun load(vkId: Int): Optional<T>

}
