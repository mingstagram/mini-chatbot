package com.mini.ai_chatbot.chatMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public Mono<ChatMessage> saveMessage(String userId, String message, String response) {
        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .message(message)
                .response(response)
                .build();

        return chatMessageRepository.save(chatMessage);
    }
}