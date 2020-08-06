package com.kriogenik.guzeeva

import com.kriogenik.guzeeva.data.repositories.RegistrationCodeRepository
import com.kriogenik.guzeeva.data.services.MeetingTableService
import com.kriogenik.guzeeva.data.services.PersonService
import com.kriogenik.guzeeva.messaging.dsl.KeyDsl
import com.kriogenik.guzeeva.messaging.dsl.KeyboardDsl
import com.kriogenik.guzeeva.messaging.model.Key
import com.kriogenik.guzeeva.model.*
import com.kriogenik.guzeeva.resources.StringResources
import com.kriogenik.guzeeva.resources.StringResourceManager
import com.petersamokhin.bots.sdk.clients.Group
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GuzeevaApplication{

	@Bean
	fun getGroupClient(
			@Value("\${group.id}")
			groupId: Int,
			@Value("\${group.token}")
			groupToken: String): Group{
		return Group(groupId, groupToken)
	}

}

fun main(args: Array<String>) {
	val ctx = runApplication<GuzeevaApplication>(*args)
	val adminCodes = listOf(
			RegistrationCode(code = "ABCD", role = PersonRole.Role.ADMIN),
			RegistrationCode(code = "ABCF", role = PersonRole.Role.ADMIN),
			RegistrationCode(code = "ABCE", role = PersonRole.Role.ADMIN)
	)
	ctx.getBean<RegistrationCodeRepository>().apply {
		adminCodes.map(this::save)
	}
}
