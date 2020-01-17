package com.kriogenik.guzeeva.model

import javax.persistence.*

@Entity
@Table(name = "meeting_tables")
data class MeetingTable(
        @Id
        val id: Int = 0,

        @OneToOne(optional = true, fetch = FetchType.EAGER)
        val person1: Person? = null,

        @OneToOne(optional = true, fetch = FetchType.EAGER)
        val person2: Person? = null
){
    fun isAvailable() = person1 != null && person2 != null
}
