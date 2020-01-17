package com.kriogenik.guzeeva.data.services.generator

import org.springframework.stereotype.Component
import java.util.*
import kotlin.streams.toList

@Component
class RegistrationCodeGeneratorImpl: RegistrationCodeGenerator {

    private final val leftLimit    = 48
    private final val rightLimit   = 122
    private final val resultLength = 4L
    private final val random = Random()


    override fun getNewCode(): String {
        return random.ints(leftLimit, rightLimit+1)
                .filter{ it !in (57..65) && it !in (90..97) }
                .limit(resultLength)
                .toList()
                .map(Int::toChar)
                .map(Char::toUpperCase)
                .foldRight(""){acc, c -> acc + c}
    }

}
