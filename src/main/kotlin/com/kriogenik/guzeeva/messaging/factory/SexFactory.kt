package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.model.Sex

interface SexFactory {

    fun getSex(n: Int): Sex

}