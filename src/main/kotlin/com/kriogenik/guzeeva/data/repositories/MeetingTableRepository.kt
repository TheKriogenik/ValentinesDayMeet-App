package com.kriogenik.guzeeva.data.repositories

import com.kriogenik.guzeeva.model.MeetingTable
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingTableRepository: CrudRepository<MeetingTable, Int>
