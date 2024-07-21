package com.abcoding.data.repository.chat

import com.abcoding.data.models.Chat
import com.abcoding.data.models.Message

interface ChatRepository {
    suspend fun getMessageForChat(chatId:String,page:Int,pageSize:Int):List<Message>

    suspend fun getChatsForUser(ownUserId:String):List<Chat>

    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean
}