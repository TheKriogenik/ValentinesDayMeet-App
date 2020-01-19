package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "persons")
data class Person(

        @Id
        val vkId: Int    = 0,

        @Column(updatable = true)
        val name: String = "",

        @Column(updatable = true)
        val age: String  = "",

        @Column(updatable = true)
        val bio: String  = "",

        @Enumerated(EnumType.STRING)
        @Column(updatable = true)
        val state: PersonEntityState = PersonEntityState.NOT_CREATED

)
