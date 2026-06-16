package com.mentalhealth.stresstracker.service;

import com.mentalhealth.stresstracker.model.Message;
import java.util.List;

public interface ChatService {
    List<Message> getConversation(Long user1Id, Long user2Id);
    Message sendMessage(Long senderId, Long receiverId, String content);
    void markAsRead(Long senderId, Long receiverId);
}