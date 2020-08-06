package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "registration_codes")
data class RegistrationCode(

        @Id
        @GeneratedValue
        val id: Int      = 0,

        val code: String = "",

        @Column(updatable = true)
        val isActivated: Boolean = false,

        val role: PersonRole.Role = PersonRole.Role.USER

){
        constructor(code: String): this(0, code)
}
