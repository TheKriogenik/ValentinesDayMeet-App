package com.kriogenik.guzeeva.actions

import com.kriogenik.guzeeva.actions.user.UserActions
import com.kriogenik.guzeeva.actions.user.matcher.Matcher
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.messaging.dsl.ResponseMessageDsl.Companion.message
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.messaging.model.Keyboard
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.Preference
import com.kriogenik.guzeeva.model.Sex
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import kotlin.random.Random

@Component
@ContainsStringResources
class SearchAction: Action<Person> {

    override val actionName: String = UserActions.SEARCH.toString()

    @Autowired
    private lateinit var personMatcher: Matcher<Person>

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.PROFILE_NAME)
    private lateinit var profileNameText: String

    @StringResource(StringResources.PROFILE_AGE)
    private lateinit var profileAgeText: String

    @StringResource(StringResources.PROFILE_SEX)
    private lateinit var profileSexText: String

    @StringResource(StringResources.PROFILE_PREFERENCE)
    private lateinit var profilePreferenceText: String

    @StringResource(StringResources.PROFILE_BIO)
    private lateinit var profileBioText: String

    @StringResource(StringResources.SEX_MALE)
    private lateinit var maleSex: String

    @StringResource(StringResources.SEX_FEMALE)
    private lateinit var femaleSex: String

    @StringResource(StringResources.SEX_NOT_SELECTED)
    private lateinit var notSelectedSex: String

    @StringResource(StringResources.PREFERENCE_MALE)
    private lateinit var malePreference: String

    @StringResource(StringResources.PREFERENCE_FEMALE)
    private lateinit var femalePreference: String

    @StringResource(StringResources.PREFERENCE_BOTH)
    private lateinit var bothPreference: String

    @StringResource(StringResources.PREFERENCE_NOT_SELECTED)
    private lateinit var notSelectedPreference: String

    @StringResource(StringResources.USER_SEARCH_NEW_CANDIDATE)
    private lateinit var newResultText: String

    @StringResource(StringResources.USER_SEND_REQUEST_BUTTON)
    private lateinit var sendRequestButtonText: String

    @StringResource(StringResources.NEXT_PROFILE_BUTTON_TEXT)
    private lateinit var nextProfileButtonText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.USER_SEARCH_NO_CANDIDATES)
    private lateinit var noCandidatesText: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map { person ->
            personMatcher.match(person).let{matchedPersons ->
                message.payload.flatMap{
                    it.args.firstOrNull().let{ Optional.ofNullable(it) }
                }.map{prev ->
                    matchedPersons.filter{ it.vkId != prev.toInt()}
                }.orElseGet{
                    matchedPersons
                }.let{filteredPersons ->
                    println(filteredPersons)
                    when(filteredPersons.isEmpty()){
                        true -> {
                            noMatchesMessage(message.userVkId)
                        }
                        else -> {
                            generateSearchMessageWithBio(message.userVkId, filteredPersons.getRandomPerson())
                        }
                    }
                }
            }
        }
    }

    private val noMatchesMessage = {vkId: Int ->
        message {
            userVkId = vkId
            text     = noCandidatesText
            keyboard = keyboard {
                row {
                    key {
                        text    = toMenuButtonText
                        payload = UserActions.TO_MENU.toString()
                        color   = Key.Color.POSITIVE
                    }
                }
            }
        }
    }

    private val generateSearchMessageWithBio = {vkId: Int, targetPerson: Person ->
        val bio =  """$newResultText!
        |$profileNameText: ${targetPerson.name}
        |$profileAgeText: ${targetPerson.age}
        |$profileSexText: ${showSex(targetPerson.sex)}
        |$profilePreferenceText: ${showPreference(targetPerson.preference)}
        |$profileBioText: ${targetPerson.bio}
    """.trimMargin()
        message {
            userVkId = vkId
            text = bio
            keyboard = keyboard {
                row {
                    key {
                        text = sendRequestButtonText
                        payload = "${UserActions.SEND_REQUEST}:${targetPerson.vkId}"
                        color = Key.Color.POSITIVE
                    }
                }
                row {
                    key {
                        text = nextProfileButtonText
                        payload = "${UserActions.SEARCH}:${targetPerson.vkId}"
                        color = Key.Color.DEFAULT
                    }
                }
                row {
                    key {
                        text = toMenuButtonText
                        payload = "${UserActions.TO_MENU}"
                        color = Key.Color.DEFAULT
                    }

                }
            }
        }
    }

    private fun showSex(sex: Sex): String = when(sex){
        Sex.FEMALE       -> femaleSex
        Sex.MALE         -> maleSex
        Sex.NOT_SELECTED -> notSelectedSex
    }

    private fun showPreference(preference: Preference): String = when(preference){
        Preference.MALE         -> malePreference
        Preference.FEMALE       -> femalePreference
        Preference.BOTH         -> bothPreference
        Preference.NOT_SELECTED -> notSelectedPreference
    }

    private fun List<Person>.getRandomPerson() = this[Random.nextInt(this.size)]

}
