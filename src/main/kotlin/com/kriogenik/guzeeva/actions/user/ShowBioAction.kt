package com.kriogenik.guzeeva.actions.user

import com.kriogenik.guzeeva.actions.Action
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.model.ReceivedMessage
import com.kriogenik.guzeeva.messaging.model.ResponseMessage
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl.Companion.keyboard
import com.kriogenik.guzeeva.model.Person
import com.kriogenik.guzeeva.model.Preference
import com.kriogenik.guzeeva.model.Sex
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.annotation.ContainsStringResources
import com.kriogenik.guzeeva.resources.annotation.StringResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
@ContainsStringResources
class ShowBioAction: Action<Person> {

    override val actionName: String = UserActions.SHOW_BIO.toString()

    @Autowired
    private lateinit var personService: PersonService

    @StringResource(StringResources.PROFILE_TITLE)
    private lateinit var profileTitleText: String

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

    @StringResource(StringResources.USER_CHANGE_NAME_BUTTON)
    private lateinit var changeNameButtonText: String

    @StringResource(StringResources.USER_CHANGE_SEX_BUTTON)
    private lateinit var changeSexButtonText: String

    @StringResource(StringResources.USER_CHANGE_AGE_BUTTON)
    private lateinit var changeAgeButtonText: String

    @StringResource(StringResources.USER_CHANGE_BIO_BUTTON)
    private lateinit var changeBioButtonText: String

    @StringResource(StringResources.USER_CHANGE_PREFERENCE_BUTTON)
    private lateinit var changePreferenceButtonText: String

    @StringResource(StringResources.USER_TO_MENU_BUTTON)
    private lateinit var toMenuButtonText: String

    @StringResource(StringResources.PHOTO_BIO)
    private lateinit var photo: String

    override fun perform(message: ReceivedMessage): Optional<ResponseMessage> {
        return personService.find(message.userVkId).map{ person ->
            ResponseMessage(person.vkId, generateBioView(person), keyboard, listOf(photo))
        }
    }

    private fun generateBioView(person: Person): String = """$profileTitleText
        |$profileNameText: ${person.name}
        |$profileAgeText: ${person.age}
        |$profileSexText: ${showSex(person.sex)}
        |$profilePreferenceText: ${showPreference(person.preference)}
        |$profileBioText: ${person.bio}
    """.trimMargin()

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

    private val keyboard by lazy {
        keyboard {
            row {
                key {
                    text    = changeNameButtonText
                    payload = UserActions.CHANGE_NAME.toString()
                }
                key {
                    text    = changeAgeButtonText
                    payload = UserActions.CHANGE_AGE.toString()
                }
            }
            row {
                key {
                    text    = changeBioButtonText
                    payload = UserActions.CHANGE_BIO.toString()
                }
            }
            row {
                key {
                    text    = changeSexButtonText
                    payload = UserActions.CHANGE_SEX.toString()
                }
                key {
                    text    = changePreferenceButtonText
                    payload = UserActions.CHANGE_PREFERENCE.toString()
                }
            }
            row {
                key {
                    text    = toMenuButtonText
                    payload = UserActions.TO_MENU.toString()
                }
            }
        }
    }

}
