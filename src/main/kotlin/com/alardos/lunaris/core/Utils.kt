package com.alardos.lunaris.core

import java.util.*

inline fun <T> dbg(value: T): T {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[1]
    println("Called from ${caller.fileName}:${caller.lineNumber} - $value")
    return value
}


/** Convert to sql compatible string list
 * example: 'id1','id2','id3' */
public fun List<UUID>.toSqlList():String {
    var result = ""
    for (id in this.iterator()) {
        if (result.isNotEmpty()) { result+="," }
        result += "'$id'"
    }
    return result
}
