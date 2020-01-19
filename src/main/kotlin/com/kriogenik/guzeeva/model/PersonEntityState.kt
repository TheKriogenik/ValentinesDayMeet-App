package com.kriogenik.guzeeva.model

enum class PersonEntityState: EntityState<Person> {

    ACTIVE,

    WAITING_RESPONSE,

    ANSWER_REQUEST,

    TALKING,

    NOT_CREATED,

    ENTERING_REGISTRATION_CODE,

    CHANGING_NAME,

    CHANGING_BIO

}
