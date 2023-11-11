package com.github.pavelannin.keemun

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform