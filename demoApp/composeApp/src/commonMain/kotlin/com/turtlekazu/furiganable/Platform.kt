package com.turtlekazu.furiganable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform