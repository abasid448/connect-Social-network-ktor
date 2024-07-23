package com.abcoding.data.websockets

import com.abcoding.data.models.Message

data class WsMessage(
    val fromId: String,
    val toId: String,
    val text: String,
    val timestamp: Long,
    val chatId: String?,
) {
    fun toMessage(): Message {
        return Message(
            fromId = fromId,
            toId = toId,
            text = text,
            timestamp = timestamp,
            chatId = chatId
        )
    }
}
