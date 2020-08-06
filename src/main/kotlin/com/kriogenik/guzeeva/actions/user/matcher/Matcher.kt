package com.kriogenik.guzeeva.actions.user.matcher

import java.util.*

interface Matcher<T> {

    fun match(target: T): List<T>

}