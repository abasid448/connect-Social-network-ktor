package com.abcoding.service.chat

import com.abcoding.data.repository.chat.ChatRepository
import com.abcoding.data.websockets.WsClientMessage
import com.abcoding.data.websockets.WsServerMessage
import com.abcoding.util.WebSocketObject
import com.google.gson.Gson
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(userId: String, socket: WebSocketSession) {
        onlineUsers[userId] = socket
    }

    fun onDisconnet(userId: String) {

        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId)
        }

    }

    suspend fun sendMessage(ownUserId: String, gson: Gson, message: WsClientMessage) {
        val messageEntity = message.toMessage(ownUserId)
        val wsServerMessage = WsServerMessage(
            fromId = ownUserId,
            toId = message.toId,
            text = message.text,
            timestamp = System.currentTimeMillis(),
            chatId = message.chatId
        )
        val frameText = gson.toJson(wsServerMessage)
        onlineUsers[ownUserId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))
        onlineUsers[message.toId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))
        if(!repository.doesChatByUsersExist(ownUserId, message.toId)) {
            val chatId = repository.insertChat(ownUserId, message.toId, messageEntity.id)
            repository.insertMessage(messageEntity.copy(chatId = chatId))
        } else {
            repository.insertMessage(messageEntity)
            message.chatId?.let {
                repository.updateLastMessageIdForChat(message.chatId, messageEntity.id)
            }
        }
    }
}