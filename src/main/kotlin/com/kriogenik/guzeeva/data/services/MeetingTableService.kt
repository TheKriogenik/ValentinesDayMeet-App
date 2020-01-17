package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.model.MeetingTable
import java.util.*

interface MeetingTableService {

    fun save(table: MeetingTable): Optional<MeetingTable>

    fun find(id: Int): Optional<MeetingTable>

    fun delete(table: MeetingTable)

}
