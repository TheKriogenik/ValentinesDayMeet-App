package com.kriogenik.guzeeva.state.meetingtable

import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.TableEntityState
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class MeetingTableNotUseStateTest {
    @Autowired
    private lateinit var meetingTableBusyState: MeetingTableNotUseState

    @MockBean
    private lateinit var meetingTableService: MeetingTableService

    @Test
    fun changeState(){
        val initialTable = MeetingTable(0, state = TableEntityState.FREE)
        val resultTable  = MeetingTable(0, state = TableEntityState.NOT_USE)
        Mockito.`when`(meetingTableService.find(initialTable.id))
                .thenReturn(Optional.of(initialTable))
        Mockito.`when`(meetingTableService.update(resultTable))
                .thenReturn(Optional.of(resultTable))
        assert(meetingTableBusyState.execute(initialTable) == Optional.of(resultTable))
    }

    @Test
    fun changeStateError(){
        val initialTable = MeetingTable(0, state = TableEntityState.FREE)
        Mockito.`when`(meetingTableService.find(initialTable.id))
                .thenReturn(Optional.empty())
        assert(meetingTableBusyState.execute(initialTable) == Optional.empty<MeetingTable>())
    }
}