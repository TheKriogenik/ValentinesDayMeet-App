package com.kriogenik.guzeeva.registration

import com.kriogenik.guzeeva.data.services.RegistrationCodeService
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.RegistrationCode
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class PersonRegistrationManagerTest {

    @Autowired
    private lateinit var personRegistrationManager: RegistrationManager<Person>

    @MockBean
    private lateinit var regCodeService: RegistrationCodeService

    @Test
    fun registerNonexistenceCode(){
        val nonexistenceCode = "ABCD"
        Mockito.`when`(regCodeService.findCode(nonexistenceCode))
                .thenReturn(Optional.empty())
        assert(!personRegistrationManager.register(nonexistenceCode))
    }

    @Test
    fun registerExistenceActivatedCode(){
        val existenceCode = RegistrationCode(code = "ABCD", isActivated = true)
        Mockito.`when`(regCodeService.findCode(existenceCode.code))
                .thenReturn(Optional.of(existenceCode))
        assert(!personRegistrationManager.register(existenceCode.code))
    }

    @Test
    fun registerExistenceNotActivatedCode(){
        val existenceCode = RegistrationCode(code = "ABCD", isActivated = false)
        val result = existenceCode.copy(isActivated = true)
        Mockito.`when`(regCodeService.findCode(existenceCode.code))
                .thenReturn(Optional.of(existenceCode))
        Mockito.`when`(regCodeService.activateCode(existenceCode.code))
                .thenReturn(Optional.of(result))
        assert(personRegistrationManager.register(existenceCode.code))
    }

}
