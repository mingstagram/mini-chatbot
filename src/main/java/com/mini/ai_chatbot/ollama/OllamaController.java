package com.mini.ai_chatbot.ollama;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class OllamaController {

    private static final Logger logger = LoggerFactory.getLogger(OllamaController.class);
    private final OllamaService ollamaService;

    /**
     * ✅ Ollama와 대화 (단일 응답)
     */
    @PostMapping("/chat")
    public Mono<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        String message = request.get("prompt");
        logger.info("🔹 OllamaController: /chat 요청 받음 - {}", message);
        return ollamaService.generateResponse(message);
    }

    /**
     * ✅ Ollama와 스트리밍 대화 (Server-Sent Events)
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestParam("message") String message) {
        logger.info("🔹 OllamaController: /stream 요청 받음 - {}", message);
        return ollamaService.generateStreamingResponse(message);
    }
}
