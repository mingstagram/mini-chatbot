package com.mini.ai_chatbot.chatMessage;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ChatMessageRepository extends ReactiveCrudRepository<ChatMessage, Long> {
    Mono<ChatMessage> findByUserId(String userId);
}
