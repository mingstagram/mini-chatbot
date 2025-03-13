package com.mini.ai_chatbot.chatMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/save")
    public Mono<ResponseEntity<?>> saveChatMessage(@RequestBody ChatMessage chatMessage) {
        return chatMessageService.saveMessage(chatMessage.getUserId(), chatMessage.getMessage(), chatMessage.getResponse())
                .map(savedMessage -> ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"대화 기록이 저장되었습니다.\"}"));
    }
}