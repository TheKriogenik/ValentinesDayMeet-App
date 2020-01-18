package com.kriogenik.guzeeva

import com.petersamokhin.bots.sdk.clients.Group
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
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
	runApplication<GuzeevaApplication>(*args)
}
