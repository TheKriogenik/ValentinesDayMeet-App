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
        val activated: Boolean = false

){
        constructor(code: String): this(0, code)
}
