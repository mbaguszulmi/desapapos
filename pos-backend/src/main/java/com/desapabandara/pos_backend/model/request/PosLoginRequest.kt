package com.desapabandara.pos_backend.model.request

data class PosLoginRequest(
    val username: String = "",
    val password: String = "",
)
