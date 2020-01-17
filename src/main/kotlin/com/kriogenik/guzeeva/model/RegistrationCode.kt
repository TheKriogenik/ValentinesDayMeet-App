package com.kriogenik.guzeeva.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "registration_codes")
data class RegistrationCode(

        @Id
        @GeneratedValue
        val id: Long,

        val code: String

)
