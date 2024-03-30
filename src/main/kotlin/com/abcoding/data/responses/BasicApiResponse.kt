package com.abcoding.data.responses

data class BasicApiResponse(
    val successful: Boolean,
    val message: String? = null,
//    val data: T? = null
)
