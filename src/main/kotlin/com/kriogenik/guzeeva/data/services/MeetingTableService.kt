package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.MeetingTable
import java.util.*

interface MeetingTableService {

    fun create(table: MeetingTable): Optional<MeetingTable>

    fun update(table: MeetingTable): Optional<MeetingTable>

    fun find(id: Int): Optional<MeetingTable>

    fun getFreeTables(): List<MeetingTable>

    fun getAll(): List<MeetingTable>

    fun delete(table: MeetingTable)

}
