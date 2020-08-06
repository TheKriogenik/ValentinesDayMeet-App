package com.kriogenik.guzeeva.actions.notauthenticated

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.actions.admin.AdminActions
import com.kriogenik.guzeeva.data.services.PersonRoleService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.PersonRole
import com.kriogenik.guzeeva.registration.RegistrationManager
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class PerformRegistration: Action<Person> {

    override val actionName: String = NotAuthenticatedPersonActions.PERFORM_REGISTRATION.toString()

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var personRoleService: PersonRoleService

    @Autowired
    private lateinit var registrationManager: RegistrationManager<Person>

    @StringResource(StringResources.REGISTRATION_ERROR_INVALID_CODE)
    private lateinit var invalidCodeText: String

    @StringResource(StringResources.REGISTRATION_ERROR_CODE_ALREADY_USED)
    private lateinit var codeAlreadyUsedText: String

    @StringResource(StringResources.REGISTRATION_SUCCESS_ENTER_CODE)
    private lateinit var successText: String

    @StringResource(StringResources.REGISTRATION_ENTER_YOUR_NAME)
    private lateinit var enterNameText: String

    @StringResource(StringResources.ADMIN_TO_MENU_BUTTON)
    private lateinit var toAdminMenuText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{person ->
            registrationManager.register(message.message).map{role ->
                person.copy(state = PersonEntityState.NOT_CREATED).let(personService::update).map{
                    personRoleService.addPersonRole(PersonRole(it.vkId, role)).map{
                        when(role){
                            PersonRole.Role.ADMIN -> adminSuccessMessage(person.vkId)
                            else                  -> successMessage(person.vkId)
                        }
                    }.orElseGet{
                        changingStateError(person.vkId)
                    }
                }
            }.orElseGet{
                wrongCodeErrorMessage(person.vkId).let{ Optional.of(it) }
            }
        }.orElseGet{
            strangeErrorMessage(message.userVkId).let{Optional.of(it)}
        }

    }

    private val adminSuccessMessage = {vkId: Int ->
        ResponseMessage(vkId, successText,
                Keyboard(
                        listOf(
                                listOf(
                                        Key(toAdminMenuText, AdminActions.TO_ADMIN_MENU.toString(), Key.Color.POSITIVE)
                                )
                        )
                ), listOf())
    }

    private val successMessage = {vkId: Int ->
        ResponseMessage(vkId, "$successText\n" +
                "$enterNameText:", Keyboard.empty(), listOf())
    }

    private val strangeErrorMessage = {vkId: Int ->
        ResponseMessage(vkId, "Неожиданная ошибка при регистрации!", Keyboard.empty(), listOf())
    }

    private val wrongCodeErrorMessage = {vkId: Int ->
        ResponseMessage(vkId, invalidCodeText, Keyboard.empty(), listOf())
    }

    private val changingStateError = {vkId: Int ->
        ResponseMessage(vkId, "Ошибка при установке нового статуса.", Keyboard.empty(), listOf())
    }

}
