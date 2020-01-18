package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "persons")
data class Person(

        @Id
        val vkId: Long   = 0L,

        val name: String = "",

        val age: String  = "",

        val bio: String  = "",

        @Enumerated(EnumType.STRING)
        val state: PersonEntityState = PersonEntityState.NOT_CREATED

)
