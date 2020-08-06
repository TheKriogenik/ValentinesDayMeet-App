package com.kriogenik.guzeeva.resources.adapter

import com.kriogenik.guzeeva.resources.StringResources

interface ResourceAdapter<T> {

    fun getResource(value: T): StringResources?

}