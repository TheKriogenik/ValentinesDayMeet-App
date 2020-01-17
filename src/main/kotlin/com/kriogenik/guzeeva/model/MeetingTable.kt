package com.kriogenik.guzeeva.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "meeting_tables")
data class MeetingTable(
        @Id
        @GeneratedValue
        val id: Int = 0,

        val person1: Person? = null,

        val person2: Person? = null
){
    fun isAvailable() = person1 != null && person2 != null
}
