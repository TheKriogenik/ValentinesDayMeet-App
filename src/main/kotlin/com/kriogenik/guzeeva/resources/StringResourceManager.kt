package com.kriogenik.guzeeva.resources

import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct

@Component
class StringResourceManager: ResourceManager<String> {

    lateinit var resources: Map<StringResources, String>

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${values.string.file}")
    private lateinit var filePath: String

    @PostConstruct
    fun init(){
        resources = File(filePath).readText().let(::JSONObject).let{json ->
            StringResources.values().map{resource ->
                resource to json.getString(resource.toString())
            }.toMap()
        }
    }

    override fun get(resource: Resource): String {
        return resources[resource] ?: "ERROR".also{
            log.error("Error on loading StringValue#$resource!")
        }
    }

    override fun get(name: String): String {
        return resources[StringResources.valueOf(name)] ?: "ERROR".also{
            log.error("Error on loading StringValue#$name!")
        }
    }

    override fun update(){
        init()
    }

}
