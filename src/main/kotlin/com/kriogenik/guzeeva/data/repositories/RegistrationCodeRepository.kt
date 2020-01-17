package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.RegistrationCode
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RegistrationCodeRepository: CrudRepository<RegistrationCode, Int>
