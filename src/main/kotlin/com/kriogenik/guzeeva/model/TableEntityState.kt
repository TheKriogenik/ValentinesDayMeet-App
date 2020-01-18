package com.kriogenik.guzeeva.model

import org.springframework.stereotype.Component


enum class TableEntityState: EntityState<MeetingTable> {

    FREE,

    BUSY,

    NOT_USE

}
