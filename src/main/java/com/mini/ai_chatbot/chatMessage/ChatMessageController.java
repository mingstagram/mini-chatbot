package com.mini.ai_chatbot.chatMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @PostMapping("/save")
    public Mono<ResponseEntity<Map<String, String>>> saveChatMessage(@RequestBody ChatMessage chatMessage) {
        return chatMessageService.saveMessage(chatMessage.getUserId(), chatMessage.getMessage(), chatMessage.getResponse())
                .thenReturn(ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "대화 기록이 저장되었습니다."
                )))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "저장 실패"
                    )));
                });
    }

    // 📌 최근 10개 대화 불러오기 API
    @GetMapping("/history")
    public Flux<String> getChatHistory(@RequestParam("userId") String userId) {
        return chatMessageService.getRecentMessages(userId, 10);
    }

}