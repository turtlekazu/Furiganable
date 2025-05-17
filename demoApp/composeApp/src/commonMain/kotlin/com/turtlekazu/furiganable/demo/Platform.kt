package com.turtlekazu.furiganable.demo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
