package com.transferwise.assignment.moviediscover.data.state

data class SingleEvent<T>(private var initValue: T, private val defaultValue: T) {

    val value: T
        get() {
            val v = initValue
            initValue = defaultValue
            return v
        }

    val hasValue: Boolean
        get() = initValue != defaultValue

}