package com.kriogenik.guzeeva.actions.user.matcher

import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.Preference
import com.kriogenik.guzeeva.model.Sex
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PersonMatcher: Matcher<Person> {

    @Autowired
    private lateinit var personService: PersonService

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun match(target: Person): List<Person> {
        return personService.getAllByState(PersonEntityState.ACTIVE).let{
            it.filter{it.vkId != target.vkId}
        }.let{activePersons ->
            log.info("ACTIVE PERSONS: $activePersons")
            when(target.preference){
                Preference.MALE         -> listOf(Sex.MALE)
                Preference.FEMALE       -> listOf(Sex.FEMALE)
                Preference.BOTH         -> listOf(Sex.FEMALE, Sex.MALE)
                Preference.NOT_SELECTED -> listOf()
            }.let{preferenceSex ->
                activePersons.filter{
                    it.sex in preferenceSex
                }
            }.let{persons ->
                log.info("PERSONS: $persons")
                persons.filter{
                    when(it.preference){
                        Preference.FEMALE       -> target.sex == Sex.FEMALE
                        Preference.MALE         -> target.sex == Sex.MALE
                        Preference.BOTH         -> true
                        Preference.NOT_SELECTED -> false
                    }
                }
            }.also{
                println("MATCH FOR $target: $it")
            }
        }
    }

}
