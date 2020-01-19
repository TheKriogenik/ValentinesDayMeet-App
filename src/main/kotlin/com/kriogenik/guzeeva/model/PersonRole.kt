package com.kriogenik.guzeeva.model

import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

data class PersonRole(
        @Id
        val personId: Int,
        @Enumerated(EnumType.STRING)
        val role    : Role
){
    enum class Role {

        USER,

        ADMIN,

        NOT_AUTHENTICATED

    }

}
