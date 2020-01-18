package com.kriogenik.guzeeva.state.persons

import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.state.person.PersonAnswerRequestState
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class PersonAnwerRequestStateTest {

    @Autowired
    private lateinit var personActiveState: PersonAnswerRequestState

    @MockBean
    private lateinit var personService: PersonService

    @Test
    fun changeState(){
        val initialState = Person(state = PersonEntityState.ACTIVE)
        val resultState  = Person(state = PersonEntityState.ANSWER_REQUEST)
        Mockito.`when`(personService.find(initialState.vkId))
                .thenReturn(Optional.of(initialState))
        Mockito.`when`(personService.update(resultState))
                .thenReturn(Optional.of(resultState))
        assert(personActiveState.execute(initialState) == Optional.of(resultState))
    }

    @Test
    fun changeStateError(){
        val initialState = Person(state = PersonEntityState.ANSWER_REQUEST)
        Mockito.`when`(personService.find(initialState.vkId))
                .thenReturn(Optional.empty())
        assert(personActiveState.execute(initialState) == Optional.empty<Person>())
    }

}
