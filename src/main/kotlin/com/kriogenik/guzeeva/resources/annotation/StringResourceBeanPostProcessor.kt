package com.kriogenik.guzeeva.resources.annotation

import com.kriogenik.guzeeva.resources.ResourceManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
class StringResourceBeanPostProcessor: BeanPostProcessor {

    @Autowired
    private lateinit var resourceManager: ResourceManager<String>

    private final val log = LoggerFactory.getLogger(this::class.java)

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val annotations = bean.javaClass.declaredAnnotations
        annotations.forEach {classAnnotation ->
            when(classAnnotation){
                is ContainsStringResources -> {
                    val fields = bean.javaClass.declaredFields
                    fields.forEach {field ->
                        field.annotations.forEach {annotation ->
                            when(annotation){
                                is StringResource -> {
                                    val value = resourceManager[annotation.value]
                                    field.isAccessible = true
                                    field.set(bean, value)
                                }
                            }
                        }
                    }
                }
            }
        }
        return bean
    }

}
