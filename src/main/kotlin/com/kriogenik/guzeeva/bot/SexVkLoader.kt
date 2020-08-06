package com.kriogenik.guzeeva.bot

import com.kriogenik.guzeeva.messaging.factory.SexFactory
import com.kriogenik.guzeeva.model.Sex
import com.petersamokhin.bots.sdk.clients.Client
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class SexVkLoader: VkLoader<Sex> {

    @Value("\${group.token}")
    private lateinit var token: String

    @Autowired
    private lateinit var sexFactory: SexFactory

    private val baseUrl: String = "https://api.vk.com/method/"

    private val version: String = "v=5.68"

    private val methodName = "users.get"

    private final val log = LoggerFactory.getLogger(this::class.java)

    private val httpClient = SslContextFactory.Client().let {
        HttpClient(it).apply {
            this.isFollowRedirects = false
            this.start()
        }
    }

    override fun load(vkId: Int): Optional<Sex> {
        return try{
            val response = httpClient.GET(getSexUrl(vkId)).content.let{String(it)}
            JSONObject(response).getJSONArray("response").getJSONObject(0).getInt("sex").let(sexFactory::getSex)
        } catch(e: Exception){
            e.printStackTrace()
            log.error(e.message.toString())
            null
        }.let{Optional.ofNullable(it)}
    }

    private val getSexUrl = {vkId: Int ->
        baseUrl + methodName + "?" + "user_ids=$vkId" + "&" +
                "fields=sex" + "&" +
                "access_token=$token" + "&" +
                version
    }

}
