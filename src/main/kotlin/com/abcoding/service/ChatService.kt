package com.abcoding.service

import com.abcoding.data.models.Chat
import com.abcoding.data.models.Message
import com.abcoding.data.repository.chat.ChatRepository

class ChatService(
    private val chatRepository: ChatRepository
) {
    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean {
        return chatRepository.doesChatBelongToUser(chatId, userId)
    }

    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message> {
        return chatRepository.getMessageForChat(chatId, page, pageSize)
    }

    suspend fun getChatsForUser(ownUserId: String): List<Chat> {
        return chatRepository.getChatsForUser(ownUserId)
    }
}