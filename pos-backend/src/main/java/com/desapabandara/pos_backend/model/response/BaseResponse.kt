package com.desapabandara.pos_backend.model.response

data class ResponseStatus(
    val code: Int = 200,
    val message: String = ""
)

data class BaseResponse<T>(
    val data: T? = null,
    val status: ResponseStatus = ResponseStatus()
)
