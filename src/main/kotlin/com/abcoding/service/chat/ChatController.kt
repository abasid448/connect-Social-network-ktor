package com.abcoding.service.chat

import com.abcoding.data.models.Message
import com.abcoding.data.repository.chat.ChatRepository
import com.abcoding.data.websockets.WsMessage
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    private val onlineUsers = ConcurrentHashMap<String,WebSocketSession>()

    fun onJoin(chatSession: ChatSession,socket:WebSocketSession){
        onlineUsers[chatSession.userId] = socket
    }

    fun onDisconnet(userId:String){

        if (onlineUsers.containsKey(userId)){
            onlineUsers.remove(userId)
        }

    }

   suspend fun sendMessage(json:String,message: WsMessage) {
       onlineUsers[message.fromId]?.send(Frame.Text(json))
       onlineUsers[message.toId]?.send(Frame.Text(json))
       val messageEntity = message.toMessage()
       repository.insertMessage(messageEntity)
       if (!repository.doesChatByUsersExist(message.fromId, message.toId)) {
           repository.insertChat(message.fromId, message.toId, messageEntity.id)
       } else {
           message.chatId?.let {
               repository.updateLastMessageIdForChat(message.chatId, messageEntity.id)
           }
       }
   }
}