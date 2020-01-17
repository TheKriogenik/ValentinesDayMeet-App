package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.RegistrationCodeRepository
import com.kriogenik.guzeeva.data.services.generator.RegistrationCodeGenerator
import com.kriogenik.guzeeva.model.RegistrationCode
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class RegistrationCodeServiceTest {

    @Autowired
    private lateinit var registrationCodeService: RegistrationCodeService

    @MockBean
    private lateinit var registrationCodeRepository: RegistrationCodeRepository

    @MockBean
    private lateinit var registrationCodeGenerator: RegistrationCodeGenerator

    @Test
    fun findExistenceRegistrationCode(){
        val existenceRegistrationCode = RegistrationCode(1, "ABCD")
        Mockito.`when`(registrationCodeRepository.findByCode(existenceRegistrationCode.code))
                .thenReturn(Optional.of(existenceRegistrationCode))
        assert(registrationCodeService.findCode(existenceRegistrationCode.code)
                == Optional.of(existenceRegistrationCode))
    }

    @Test
    fun findNonexistentRegistrationCode(){
        val nonExistentCode = "ABCD"
        Mockito.`when`(registrationCodeRepository.findByCode(nonExistentCode))
                .thenReturn(Optional.empty())
        assert(registrationCodeService.findCode(nonExistentCode)
                == Optional.empty<RegistrationCode>())
    }

    @Test
    fun saveNewCode(){
        val newRegistrationCode = RegistrationCode("ABCD")
        Mockito.`when`(registrationCodeRepository.findByCode(newRegistrationCode.code))
                .thenReturn(Optional.empty())
        Mockito.`when`(registrationCodeRepository.save(newRegistrationCode))
                .thenReturn(newRegistrationCode)
        Mockito.`when`(registrationCodeGenerator.getNewCode())
                .thenReturn("ABCD")
        assert(registrationCodeService.getNewCode() == newRegistrationCode)
    }

    @Test
    fun saveNewCodeWithSecondTry(){
        val existenceRegistrationCode = RegistrationCode("ABCD")
        val newRegistrationCode = RegistrationCode("ABCE")
        Mockito.`when`(registrationCodeRepository.findByCode(existenceRegistrationCode.code))
                .thenReturn(Optional.of(existenceRegistrationCode))
        Mockito.`when`(registrationCodeRepository.findByCode(newRegistrationCode.code))
                .thenReturn(Optional.empty())
        Mockito.`when`(registrationCodeRepository.save(newRegistrationCode))
                .thenReturn(newRegistrationCode)

        Mockito.`when`(registrationCodeGenerator.getNewCode())
                .thenReturn(existenceRegistrationCode.code)
                .thenReturn(newRegistrationCode.code)
        assert(registrationCodeService.getNewCode() == newRegistrationCode)
    }

}
