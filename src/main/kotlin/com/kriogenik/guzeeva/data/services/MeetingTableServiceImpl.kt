package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.MeetingTableRepository
import com.kriogenik.guzeeva.model.MeetingTable
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MeetingTableServiceImpl: MeetingTableService {

    @Autowired
    private lateinit var repository: MeetingTableRepository

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun save(table: MeetingTable): Optional<MeetingTable> {
        return try{
            repository.save(table).let{Optional.of(it)}
        } catch(e: Exception){
            log.error(e.message)
            Optional.empty()
        }
    }

    override fun find(id: Int): Optional<MeetingTable> {
        return repository.findById(id)
    }

    override fun delete(table: MeetingTable) {
        return repository.delete(table)
    }

}
