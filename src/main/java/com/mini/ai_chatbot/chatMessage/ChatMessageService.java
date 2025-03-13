package com.mini.ai_chatbot.chatMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Void> saveMessage(String userId, String message, String response) {
        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .message(message)
                .response(response)
                .build();

        Mono<Void> dbSave = chatMessageRepository.save(chatMessage).then();

        // 2️Redis 캐싱 (Key: "chat:userId", Value: 메시지)
        Mono<Boolean> redisSave = redisTemplate.opsForList()
                .rightPush("chat:" + chatMessage.getUserId(), chatMessage.getMessage())
                .thenReturn(true);

        return Mono.when(dbSave, redisSave).then();
    }

    // Redis에서 최근 N개 메시지 가져오기
    public Flux<String> getRecentMessages(String userId, int count) {
        return redisTemplate.opsForList()
                .range("chat:" + userId, -count, -1); // 가장 최근 N개 가져오기
    }

}