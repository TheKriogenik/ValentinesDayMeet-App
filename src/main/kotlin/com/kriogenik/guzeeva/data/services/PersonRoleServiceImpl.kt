package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.PersonRoleRepository
import com.kriogenik.guzeeva.model.PersonRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonRoleServiceImpl: PersonRoleService {

    @Autowired
    private lateinit var repository: PersonRoleRepository

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun getPersonRole(personId: Int): Optional<PersonRole> {
        return repository.findById(personId)
    }

    override fun changePersonRole(personRole: PersonRole): Optional<PersonRole> {
        return repository.findById(personRole.personId).map{
            repository.save(personRole)
                    .let{
                        Optional.ofNullable(it)
                    }
        }.orElseGet{
            Optional.empty()
        }
    }

    override fun addPersonRole(personRole: PersonRole): Optional<PersonRole> {
        return repository.findById(personRole.personId).map{
            Optional.empty<PersonRole>()
        }.orElseGet{
            try{
                repository.save(personRole).let{Optional.ofNullable(it)}
            } catch (e: Exception){
                log.error(e.message)
                Optional.empty()
            }
        }
    }

}
