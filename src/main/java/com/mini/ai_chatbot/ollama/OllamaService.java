package com.mini.ai_chatbot.ollama;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OllamaService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);
    private final WebClient.Builder webClientBuilder;

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    /**
     * ✅ Ollama API를 통해 단일 응답을 가져오는 함수
     */
    public Mono<Map<String, String>> generateResponse(String message) {
        logger.info("🔹 OllamaService: 요청 받음 - {}", message);

        WebClient webClient = webClientBuilder.baseUrl(ollamaApiUrl).build();

        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", model,
                        "prompt", message,
                        "format", "markdown",
                        "stream", false
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .subscribeOn(Schedulers.boundedElastic())
                .map(response -> {
                    logger.info("🔹 Ollama 최종 응답: {}", response);
                    return Map.of("response", response.get("response").toString());
                })
                .onErrorResume(e -> {
                    logger.error("❌ Ollama 요청 중 오류 발생:", e);
                    return Mono.just(Map.of("response", "⚠️ 오류 발생"));
                });
    }

    /**
     * ✅ Ollama API를 통해 스트리밍 응답을 처리하는 함수 (Server-Sent Events)
     */
    public Flux<ServerSentEvent<String>> generateStreamingResponse(String message) {
        logger.info("🔹 OllamaService: 스트리밍 요청 받음 - {}", message);

        WebClient webClient = webClientBuilder.baseUrl("http://192.168.250.250:11434").build();

        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "model", "phi4",
                        "prompt", message,
                        "stream", true
                ))
                .retrieve()
                .bodyToFlux(Map.class)
                .bufferUntil(response -> {
                    String text = response.get("response").toString();
                    return text.endsWith(".") || text.endsWith("\n");  // ✅ 문장 단위로 버퍼링
                })
                .map(responseList -> {
                    String fullText = responseList.stream()
                            .map(response -> response.get("response").toString())
                            .collect(Collectors.joining());

                    logger.info("🔹 문장 단위 응답: {}", fullText);

                    return ServerSentEvent.<String>builder()
                            .data(fullText)  // ✅ 문장 단위로 전송
                            .build();
                })
                .onErrorResume(e -> {
                    logger.error("❌ 스트리밍 중 오류 발생:", e);
                    return Flux.just(ServerSentEvent.<String>builder()
                            .data("⚠️ 오류 발생")
                            .build());
                });
    }


}