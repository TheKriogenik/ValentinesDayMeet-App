package com.kriogenik.guzeeva.handlers.received.roles

import com.kriogenik.guzeeva.actions.user.UserActions
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.handlers.PersonActionFactory
import com.kriogenik.guzeeva.handlers.received.PersonReceivedMessageHandler
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.PersonEntityState
import com.kriogenik.guzeeva.model.PersonRole
import com.kriogenik.guzeeva.model.Sex
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserPersonReceivedMessageHandler: PersonReceivedMessageHandler {

    override val personRole: PersonRole.Role = PersonRole.Role.USER

    @Autowired
    private lateinit var personActionFactory: PersonActionFactory

    @Autowired
    private lateinit var personService: PersonService

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(target: ReceivedMessage): Optional<ResponseMessage> {
        log.info("RECEIVED MESSAGE: $target")
        return personService.find(target.userVkId).flatMap{person ->
            when(person.inNonHandlingState()){
                true -> Optional.empty()
                else -> when{
                    person.name.isBlank() -> UserActions.CHANGE_NAME.toString().let(personActionFactory::getPersonAction)
                    person.age.isBlank()  -> UserActions.CHANGE_AGE.toString().let(personActionFactory::getPersonAction)
                    person.bio.isBlank()  -> UserActions.CHANGE_BIO.toString().let(personActionFactory::getPersonAction)
                    person.sex == Sex.NOT_SELECTED -> UserActions.CHANGE_SEX.toString().let(personActionFactory::getPersonAction)
                    else                  -> target.payload.map{
                        it.action
                    }
                }.map{ action ->
                    action.perform(target)
                }.orElseGet{
                    Optional.of(errorHandlingMessage(person.vkId))
                }
            }
        }
    }

    private fun Person.inNonHandlingState() = this.state in listOf(
            PersonEntityState.WAITING_RESPONSE
    )

    private val errorHandlingMessage = {vkId: Int ->
        ResponseMessage(vkId, "Не найден нужный обработчик!", Keyboard(listOf(
                listOf(Key("В меню.", UserActions.TO_MENU.toString(), Key.Color.DEFAULT))
        )), listOf())
    }

}
