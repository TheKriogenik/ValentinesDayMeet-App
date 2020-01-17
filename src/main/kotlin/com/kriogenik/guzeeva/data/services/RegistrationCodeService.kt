package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.RegistrationCode
import java.util.*

interface RegistrationCodeService {

    fun getNewCode(): RegistrationCode

    fun findCode(): Optional<RegistrationCode>

}
