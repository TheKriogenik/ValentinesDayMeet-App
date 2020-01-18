package com.kriogenik.guzeeva.model

enum class PersonEntityState: EntityState<Person> {

    ACTIVE,

    WAITING_RESPONSE,

    ANSWER_REQUEST,

    TALKING,

    NOT_CREATED

}
