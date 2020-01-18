package com.kriogenik.guzeeva.state.meetingtable

import com.kriogenik.guzeeva.model.EntityState
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.state.State
import com.kriogenik.guzeeva.state.StateFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MeetingTableStateFactory: StateFactory<MeetingTable> {

    @Autowired
    private lateinit var meetingTableFreeState: MeetingTableFreeState

    @Autowired
    private lateinit var meetingTableBusyState: MeetingTableBusyState

    @Autowired
    private lateinit var meetingTableNotUseState: MeetingTableNotUseState

    override fun getState(stateEnum: EntityState<MeetingTable>): State<MeetingTable> {
        return when(stateEnum.toString()){
            "FREE" -> {
                meetingTableFreeState
            }
            "BUSY" -> {
                meetingTableBusyState
            }
            "NOT_USE" -> {
                meetingTableNotUseState
            }
            else -> meetingTableNotUseState
        }
    }

}
