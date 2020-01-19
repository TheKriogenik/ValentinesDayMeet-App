package com.kriogenik.guzeeva.registration

interface RegistrationManager<T>{

    fun register(registrationCode: String): Boolean

}
