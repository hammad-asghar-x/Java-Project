package com.mentalhealth.stresstracker.controller;

import com.mentalhealth.stresstracker.model.Message;
import com.mentalhealth.stresstracker.model.User;
import com.mentalhealth.stresstracker.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Get messages between current user and another user
    @GetMapping("/messages/{receiverId}")
    public ResponseEntity<List<Message>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long receiverId) {
        
        // Mark incoming messages as read
        chatService.markAsRead(receiverId, currentUser.getId());
        List<Message> messages = chatService.getConversation(currentUser.getId(), receiverId);
        return ResponseEntity.ok(messages);
    }

    // Send a new message
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long receiverId,
            @RequestParam String content) {
        
        Message savedMessage = chatService.sendMessage(currentUser.getId(), receiverId, content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", savedMessage);
        return ResponseEntity.ok(response);
    }
}