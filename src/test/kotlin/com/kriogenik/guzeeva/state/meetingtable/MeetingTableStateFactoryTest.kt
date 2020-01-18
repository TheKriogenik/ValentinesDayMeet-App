package com.kriogenik.guzeeva.state.meetingtable

import com.kriogenik.guzeeva.model.EntityState
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.TableEntityState
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MeetingTableStateFactoryTest {

    @Autowired
    private lateinit var stateFactory: MeetingTableStateFactory

    @Test
    fun getAllPossibleStates(){
       TableEntityState.values().map{
           stateFactory.getState(it)
       }.all{
           (TableEntityState.values().toList().map(TableEntityState::toString))
                   .contains(it.toString())
       }
    }

}
