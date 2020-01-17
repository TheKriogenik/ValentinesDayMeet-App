package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "persons")
class Person(

        @Id
        val vkId: Long   = 0L,

        val name: String = "",

        val age: String  = "",

        val bio: String  = "",

        @Enumerated(EnumType.STRING)
        val state: PersonState = PersonState.NOT_CREATED

)
