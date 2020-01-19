package com.kriogenik.guzeeva.data.services

import com.kriogenik.guzeeva.data.repositories.PersonRoleRepository
import com.kriogenik.guzeeva.model.PersonRole
import org.hibernate.exception.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*
import javax.transaction.Transactional

@SpringBootTest
@Transactional
class PersonRoleServiceTest {

    @Autowired
    private lateinit var personRoleService: PersonRoleService

    //@MockBean
    @Autowired
    private lateinit var personRoleRepository: PersonRoleRepository

    @Test
    fun createNewPersonRole(){
        val newPersonRole = PersonRole(1, PersonRole.Role.USER)
        /*Mockito.`when`(personRoleRepository.save(newPersonRole))
                .thenReturn(newPersonRole)*/
        assert(personRoleService.addPersonRole(newPersonRole).map{
            it == newPersonRole
        }.orElse(false))
    }

    @Test
    @Throws(ConstraintViolationException::class)
    fun createExistencePersonRole(){
        personRoleRepository.save(PersonRole(1, PersonRole.Role.NOT_AUTHENTICATED))
        val newPersonRole = PersonRole(1, PersonRole.Role.USER)
        /*Mockito.`when`(personRoleRepository.save(newPersonRole))
                .thenThrow(ConstraintViolationException::class.java)*/
        assert(personRoleService.addPersonRole(newPersonRole) == Optional.empty<PersonRole>())
    }

    @Test
    fun updateNonexistencePersonRole(){
        val newPersonRole = PersonRole(1, PersonRole.Role.USER)
        /*Mockito.`when`(personRoleRepository.findById(1))
                .thenReturn(Optional.empty())*/
        assert(personRoleService.changePersonRole(newPersonRole).isEmpty)
    }

    @Test
    fun updateExistencePersonRole(){
        val oldPersonRole = PersonRole(1, PersonRole.Role.NOT_AUTHENTICATED)
        personRoleRepository.save(oldPersonRole)
        val newPersonRole = PersonRole(1, PersonRole.Role.USER)
        /*Mockito.`when`(personRoleRepository.)*/
        assert(personRoleService.changePersonRole(newPersonRole).map{
            it == newPersonRole
        }.orElse(false))
    }

}