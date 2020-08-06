package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.PersonRepository
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.Sex
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonServiceImpl: PersonService {

    @Autowired
    private lateinit var repository: PersonRepository

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun create(person: Person): Optional<Person> {
        return try{
            repository.save(person).let{Optional.of(it)}
        } catch(e: Exception){
            log.error(e.message)
            Optional.empty()
        }
    }

    override fun update(person: Person): Optional<Person> {
        return repository.findById(person.vkId).map{
                    repository.save(person)
                }.also{
            log.info("UPDATED: $it")
        }
    }

    override fun find(id: Int): Optional<Person> {
        return repository.findById(id)
    }

    override fun getAllBySex(sex: Sex): List<Person> {
        return repository.findBySex(sex)
    }

    override fun getAllByState(state: PersonEntityState): List<Person> {
        return repository.findByState(state)
    }

    override fun delete(person: Person) {
        return repository.delete(person)
    }
}