package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "person_role")
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
