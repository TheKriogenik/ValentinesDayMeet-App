package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.TableEntityState
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingTableRepository: CrudRepository<MeetingTable, Int>{

    fun getByState(state: TableEntityState): List<MeetingTable>

}
