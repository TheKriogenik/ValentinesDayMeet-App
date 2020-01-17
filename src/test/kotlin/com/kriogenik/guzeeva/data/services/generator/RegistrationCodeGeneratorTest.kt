package com.kriogenik.guzeeva.data.services.generator

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RegistrationCodeGeneratorTest {

    @Autowired
    private lateinit var registrationCodeGenerator: RegistrationCodeGenerator

    @Test
    fun generateValidCode(){
        val executionTimes = 100
        (0 until executionTimes).asSequence().map{
            registrationCodeGenerator.getNewCode()
        }.map{code ->
            code.length == 4
                    && code.all(Char::isLetterOrDigit)
                    && code.filter(Char::isLetter).all(Char::isUpperCase)
        }.reduce{acc, x -> acc && x}.let(::assert)
    }

}
