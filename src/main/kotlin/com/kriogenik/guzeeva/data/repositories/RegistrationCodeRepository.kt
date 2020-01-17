package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.RegistrationCode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RegistrationCodeRepository: CrudRepository<RegistrationCode, Int>{

    fun findByCode(code: String): Optional<RegistrationCode>

}
