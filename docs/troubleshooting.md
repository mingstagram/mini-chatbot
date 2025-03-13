## 1️⃣ Ollama API 연동 문제
### 🔍 문제
- OllamaChatModel을 사용하여 응답을 받으려 했으나 `NullPointerException` 발생
- `org.springframework.ai.ollama.api.OllamaApi$Message.role()` 호출 시 `current`가 null이라는 오류 발생
### ✅ 해결 방법
#### 1. WebClient 방식으로 전환
- `OllamaChatModel.call()` 대신 `WebClient`를 이용하여 직접 API 호출
- `WebClient.Builder`를 사용하여 `POST /api/generate` 요청 수행

#### 2. WebClient 요청 수정
- `format: "json"` 으로 설정 (기본적으로 `markdown`은 지원되지 않음)
- `stream: true` 옵션을 포함하여 SSE 방식의 스트리밍 활성화

---

## 2️⃣ 스트리밍 데이터가 문자 단위로 잘리는 문제
### 🔍 문제
- `Ollama API`의 응답이 한 글자씩 잘려서 수신됨 (`"response": "한"`, `"response": "국"`, ...)
- 마크다운이 깨지고, 응답이 자연스럽게 연결되지 않음
### ✅ 해결 방법
#### 1. 서버에서 응답을 문자열로 누적
```
StringBuilder responseBuffer = new StringBuilder();
return webClient.post()
    .uri("/api/generate")
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(Map.of("model", "phi4", "prompt", message, "stream", true))
    .retrieve()
    .bodyToFlux(Map.class)
    .map(response -> {
        String text = response.get("response").toString();
        responseBuffer.append(text);
        return ServerSentEvent.builder().data(responseBuffer.toString()).build();
    });
```
#### 2. 프론트엔드에서 한 글자씩 추가하는 방식 개선
- 기존 방식: 한 글자씩 추가 → 연결되지 않은 문장
- 개선 방식: 한 번에 문장 단위로 추가

---

## 3️⃣ Markdown 형식이 적용되지 않는 문제
### 🔍 문제
- Ollama API에서 `format: "markdown"`으로 요청했으나,` invalid format: "\"markdown\""` 오류 발생
- 응답이 일반 텍스트로만 전달되어 프론트에서 `ReactMarkdown`이 적용되지 않음
### ✅ 해결 방법
#### 1. Ollama API가 지원하는 형식 확인
- `format: "json"`으로 변경하고, 응답을 JSON 형태로 받도록 수정
- JSON 응답에서 마크다운을 포함하는 필드를 직접 추출 (`response.markdown`)
#### 2. 프론트엔드에서 Markdown 처리 방식 개선
- `ReactMarkdown` 라이브러리 사용
- `rehype-raw`, `remark-gfm` 플러그인 추가하여 올바르게 렌더링
```text
import ReactMarkdown from "react-markdown";
import rehypeHighlight from "rehype-highlight";
import rehypeRaw from "rehype-raw";
import remarkGfm from "remark-gfm";

<ReactMarkdown rehypePlugins={[rehypeRaw, rehypeHighlight]} remarkPlugins={[remarkGfm]}>
    {message.text}
</ReactMarkdown>
```

---

## 4️⃣ SSE(EventSource) 오류 및 응답이 끊기는 문제
### 🔍 문제
- EventSource에서 onerror 이벤트가 자주 발생하며 스트리밍이 중단됨
- 서버에서 200 OK 응답을 받았지만, 클라이언트에서 ⚠️ 응답을 가져올 수 없습니다. 출력됨
### ✅ 해결 방법
#### 1. 서버에서 text/event-stream 명확히 설정
```text
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> chatStream(@RequestParam("message") String message) { ... }
```
#### 2. 프론트엔드 EventSource 핸들러 개선
- 연결이 끊어지면 자동으로 재연결하도록 설정
```text
eventSource.onerror = () => {
    console.warn("⚠️ SSE 연결 끊김, 3초 후 재연결 시도...");
    setTimeout(() => sendMessage(), 3000);
};
```

---

