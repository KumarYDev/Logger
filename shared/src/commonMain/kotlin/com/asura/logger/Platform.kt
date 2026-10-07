package com.asura.logger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform