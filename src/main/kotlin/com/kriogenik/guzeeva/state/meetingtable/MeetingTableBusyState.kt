package com.kriogenik.guzeeva.state.meetingtable

import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.model.EntityState
import com.kriogenik.guzeeva.model.MeetingTable
import com.kriogenik.guzeeva.model.TableEntityState
import com.kriogenik.guzeeva.state.State
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class MeetingTableBusyState: State<MeetingTable> {

    override val state: EntityState<MeetingTable> = TableEntityState.BUSY

    @Autowired
    private lateinit var service: MeetingTableService

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun execute(context: MeetingTable): Optional<MeetingTable> {
        log.debug("Changing state of MeetingTable#${context.id}.")
        return context.copy(state = TableEntityState.BUSY).let(service::update).map {
            log.debug("State of MeetingTable#${context.id} changed.")
            Optional.of(it)
        }.orElseGet{
            log.error("State of MeetingTable#${context.id} not changed!")
            Optional.empty<MeetingTable>()
        }
    }

}
