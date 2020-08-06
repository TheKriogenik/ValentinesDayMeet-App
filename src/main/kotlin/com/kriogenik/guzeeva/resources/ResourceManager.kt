package com.kriogenik.guzeeva.resources

interface ResourceManager<T> {

    operator fun get(resource: Resource): T

    operator fun get(name: String): T

    fun update()

}
