package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.RegistrationCode
import java.util.*

interface RegistrationCodeService {

    fun getNewCode(): RegistrationCode

    fun findCode(code: String): Optional<RegistrationCode>

    fun activateCode(code: String): Optional<RegistrationCode>

}
