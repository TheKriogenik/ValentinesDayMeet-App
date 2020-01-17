package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.MeetingTableRepository
import com.kriogenik.guzeeva.model.MeetingTable
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.hibernate.exception.ConstraintViolationException
import java.util.*

@SpringBootTest
class MeetingTableServiceTest {

    @Autowired
    private lateinit var meetingTableService: MeetingTableService

    @MockBean
    private lateinit var meetingTableRepository: MeetingTableRepository

    @Test
    fun addNewMeetingTable(){
        val newMeetingTable = MeetingTable(0)
        Mockito.`when`(meetingTableRepository.save(newMeetingTable))
                .thenReturn(newMeetingTable)
        assert(meetingTableService.save(newMeetingTable) == Optional.of(newMeetingTable))
    }

    @Test
    @Throws(ConstraintViolationException::class)
    fun addExistenceMeetingTable(){
        val existenceMeetingTable = MeetingTable(0)
        Mockito.`when`(meetingTableRepository.save(existenceMeetingTable))
                .thenThrow(ConstraintViolationException::class.java)
        assert(meetingTableService.save(existenceMeetingTable) == Optional.empty<MeetingTable>())
    }

    @Test
    fun findExistenceMeetingTable(){
        val existenceMeetingTable = MeetingTable(0)
        Mockito.`when`(meetingTableRepository.findById(existenceMeetingTable.id))
                .thenReturn(Optional.of(existenceMeetingTable))
        assert(meetingTableService.find(existenceMeetingTable.id) == Optional.of(existenceMeetingTable))
    }

    @Test
    fun findNonexistentMeetingTable(){
        val nonexistentMeetingTableId = 0
        Mockito.`when`(meetingTableRepository.findById(nonexistentMeetingTableId))
                .thenReturn(Optional.empty())
        assert(meetingTableService.find(nonexistentMeetingTableId) == Optional.empty<MeetingTable>())
    }

}
