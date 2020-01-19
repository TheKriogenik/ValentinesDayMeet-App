package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.RegistrationCodeRepository
import com.kriogenik.guzeeva.data.services.generator.RegistrationCodeGenerator
import com.kriogenik.guzeeva.model.RegistrationCode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegistrationCodeServiceImpl: RegistrationCodeService {

    @Autowired
    private lateinit var repository: RegistrationCodeRepository

    @Autowired
    private lateinit var codeGenerator: RegistrationCodeGenerator

    override fun getNewCode(): RegistrationCode {
        val code = codeGenerator.getNewCode()
        return repository.findByCode(code)
                .map{
                    getNewCode()
                }.orElseGet{
                    RegistrationCode(code)
                            .let(repository::save)
                }
    }

    override fun findCode(code: String): Optional<RegistrationCode> {
        return repository.findByCode(code)
    }

    override fun activateCode(code: String): Optional<RegistrationCode> {
        return repository.findByCode(code).map{
            it.copy(isActivated = true).let(repository::save)
        }
    }

}
