package com.kriogenik.guzeeva.messaging.factory

import com.kriogenik.guzeeva.model.Sex
import org.springframework.stereotype.Component

@Component
class SexFactoryImpl: SexFactory {

    override fun getSex(n: Int): Sex {
        return when(n){
            1    -> Sex.FEMALE
            2    -> Sex.MALE
            else -> Sex.NOT_SELECTED
        }
    }

}
