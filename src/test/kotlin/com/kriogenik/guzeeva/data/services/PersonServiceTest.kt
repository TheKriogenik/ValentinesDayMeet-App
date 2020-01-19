package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.PersonRepository
import com.kriogenik.guzeeva.model.Person
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private lateinit var personService: PersonService

    @MockBean
    private lateinit var personRepository: PersonRepository

    @Test
    fun createNonexistentPerson(){
        val nonexistentPerson = Person(0)
        Mockito.`when`(personRepository.save(nonexistentPerson))
                .thenReturn(nonexistentPerson)
        assert(personService.create(nonexistentPerson) == Optional.of(nonexistentPerson))
    }

    @Test
    @Throws(ConstraintViolationException::class)
    fun createExistentPerson(){
        val existentPerson = Person(0)
        Mockito.`when`(personRepository.save(existentPerson))
                .thenThrow(ConstraintViolationException::class.java)
        assert(personService.create(existentPerson) == Optional.empty<Person>())
    }

    @Test
    fun findExistentPerson(){
        val existentPerson = Person(0)
        Mockito.`when`(personRepository.findById(existentPerson.vkId))
                .thenReturn(Optional.of(existentPerson))
        assert(personService.find(existentPerson.vkId) == Optional.of(existentPerson))
    }

    @Test
    fun findNonexistentPerson(){
        val nonExistentPerson = Person(0)
        Mockito.`when`(personRepository.findById(nonExistentPerson.vkId))
                .thenReturn(Optional.empty())
        assert(personService.find(nonExistentPerson.vkId) == Optional.empty<Person>())
    }

    @Test
    fun updateExistentPerson(){
        val existentPerson = Person(0)
        val updatedPerson = Person(0, "Test")
        Mockito.`when`(personRepository.findById(existentPerson.vkId))
                .thenReturn(Optional.of(existentPerson))
        Mockito.`when`(personRepository.save(updatedPerson))
                .thenReturn(updatedPerson)
        assert(personService.update(updatedPerson) == Optional.of(updatedPerson))
    }

    @Test
    fun updateNonexistentPerson(){
        val updatedPerson = Person(0, "Test")
        Mockito.`when`(personRepository.findById(updatedPerson.vkId))
                .thenReturn(Optional.empty())
        Mockito.`when`(personRepository.save(updatedPerson))
                .thenReturn(updatedPerson)
        assert(personService.update(updatedPerson) == Optional.empty<Person>())
    }

}
