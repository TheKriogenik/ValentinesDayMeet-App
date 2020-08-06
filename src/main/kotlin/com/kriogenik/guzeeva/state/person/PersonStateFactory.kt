package com.kriogenik.guzeeva.state.person

import com.kriogenik.guzeeva.model.EntityState
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.state.State
import com.kriogenik.guzeeva.state.StateFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonStateFactory: StateFactory<Person> {

    @Autowired
    private lateinit var states: List<State<Person>>

    override fun getState(stateEnum: EntityState<Person>): Optional<State<Person>> {
        return states.find{stateEnum == it.state}?.let{
            Optional.of(it)
        } ?: Optional.empty()
    }

}
