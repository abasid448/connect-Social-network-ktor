package com.abcoding.data.repository.chat

import com.abcoding.data.models.Chat
import com.abcoding.data.models.Message

interface ChatRepository {
    suspend fun getMessageForChat(chatId: String, page: Int, pageSize: Int): List<Message>

    suspend fun getChatsForUser(ownUserId: String): List<Chat>

    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean

    suspend fun insertMessage(message: Message)

    suspend fun insertChat(userId1: String, userId2: String, messageId: String)

    suspend fun doesChatByUsersExist(userId1: String, userId2: String): Boolean

    suspend fun updateLastMessageIdForChat(chatId: String, lastMessageId: String)
}