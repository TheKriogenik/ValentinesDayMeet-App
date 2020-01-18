package com.kriogenik.guzeeva.state.persons

import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.state.StateFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PersonStateFactoryTest {

    @Autowired
    private lateinit var stateFactory: StateFactory<Person>

    @Test
    fun getAllPossibleTests(){
        PersonEntityState.values().map{state ->
            stateFactory.getState(state)
        }.all{
            (PersonEntityState.values().toList().map(PersonEntityState::toString))
                    .contains(it.toString())
        }
    }

}
