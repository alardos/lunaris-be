package com.alardos.lunaris.core

inline fun <T> dbg(value: T): T {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[1]
    println("Called from ${caller.fileName}:${caller.lineNumber} - $value")
    return value
}