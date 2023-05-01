package com.orbital.cee.core

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}