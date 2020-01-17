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
        val id: Int      = 0,

        val code: String = ""

){
        constructor(code: String): this(0, code)
}
