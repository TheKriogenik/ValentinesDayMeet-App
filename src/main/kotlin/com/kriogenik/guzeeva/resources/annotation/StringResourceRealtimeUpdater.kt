package com.kriogenik.guzeeva.resources.annotation

import com.kriogenik.guzeeva.resources.ResourceManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File

@Component
class StringResourceRealtimeUpdater {

    @Autowired
    private lateinit var stringResourceBPP: StringResourceBeanPostProcessor

    @Autowired
    private lateinit var context: ApplicationContext

    @Autowired
    private lateinit var resourceManager: ResourceManager<String>

    @Value("\${values.string.file}")
    private lateinit var filePath: String

    private var fileSize: Long = 0L

    private final val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(initialDelay = 10000L, fixedDelay = 10000L)
    fun updateStringResources(){
        log.info("Start updating")
        val newSize = getFileSize()
        resourceManager.update()
        if(newSize != fileSize){
            log.info("Perform string values updating")
            fileSize = newSize
            context.getBeansWithAnnotation(ContainsStringResources::class.java).map{(name, bean) ->
                stringResourceBPP.postProcessBeforeInitialization(bean, name)
            }
        }
    }

    private fun getFileSize(): Long{
        return File(filePath).length()
    }

}