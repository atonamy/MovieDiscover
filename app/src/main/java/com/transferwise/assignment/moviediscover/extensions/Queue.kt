package com.transferwise.assignment.moviediscover.extensions

import java.util.*

inline fun <T> Queue<T>.dequeAll(step: (T) -> Unit) {
    do {
        step(poll() ?: break)
    } while(isNotEmpty())
}