# 🏗️ AI 챗봇 프로젝트 아키텍처

## 📌 개요
이 문서는 **AI 챗봇 프로젝트의 전체 아키텍처**를 설명하며, **Backend, Frontend, AI 모델, Database, Redis** 간의 연계를 정리합니다.  
이 프로젝트는 **Spring WebFlux + Ollama + Redis + React** 기반으로 구성되며, 비동기 데이터 처리 및 실시간 챗봇 서비스를 제공합니다.

---

## 📌 시스템 아키텍처 다이어그램

```plaintext
+-------------+        +-------------------+        +------------------+
|  React UI   | <----> | Spring WebFlux API| <----> | Ollama AI Model  |
+-------------+        +-------------------+        +------------------+
      |                        |                          |
      v                        v                          v
+-------------+        +-------------------+        +------------------+
| EventSource | <----> | Redis (세션 & 캐싱) | <----> | MariaDB (R2DBC) |
+-------------+        +-------------------+        +------------------+
```
 

### ✅ Frontend (React)

-  사용자가 입력한 메시지를 EventSource(SSE) 를 통해 백엔드와 실시간 통신
-  ReactMarkdown 을 활용하여 AI 응답을 마크다운 형식으로 렌더링 Backend (Spring WebFlux)

### ✅ WebFlux 기반 비동기 API 제공
- Ollama AI 모델 과 WebClient 를 통해 통신
- Redis 를 활용한 세션 캐싱 및 데이터 저장
- MariaDB (R2DBC) 기반으로 비동기 데이터 처리

✅ Ollama AI 모델
- phi-4 모델을 활용하여 사용자 입력을 실시간으로 처리
- WebClient API 요청을 통해 챗봇 응답을 받아옴
- 응답 데이터를 스트리밍 방식으로 전달

✅ Redis (캐싱 및 세션 관리)
- 사용자의 대화 기록을 캐싱하여 빠른 응답 제공
- TTL(Time-To-Live) 설정을 통해 세션 만료 관리

✅ MariaDB (R2DBC 기반 비동기 데이터베이스)
- 사용자 데이터 저장 (대화 내역, 설정 정보 등)
- R2DBC를 활용하여 비동기 DB 처리 최적화

---

## 📌 주요 아키텍처 구성 요소

### ✅ 1. 비동기 이벤트 스트리밍 (WebFlux & SSE)
- Spring WebFlux 기반으로 비동기 REST API 구현
- Server-Sent Events (SSE) 방식으로 AI 응답을 실시간 스트리밍
-  EventSource 를 활용하여 AI 응답을 동적으로 업데이트
### ✅ 2. Ollama AI 모델 연동
- WebClient 를 사용하여 Ollama API 호출
-  시 JSON 응답 형식으로 설정하여 마크다운 지원
- Ollama의 스트리밍 응답을 Flux 로 변환하여 실시간 전송
### ✅ 3. Redis 기반 세션 관리 및 캐싱
- 사용자의 최근 대화 데이터를 Redis에 저장하여 빠른 응답 처리
- TTL(세션 유지 시간)을 설정하여 불필요한 데이터 저장 방지
- user_session:{sessionId} 형태의 키-값 저장 방식 적용
### ✅ 4. 비동기 데이터베이스 처리 (MariaDB + R2DBC)
- R2dbcRepository 를 사용하여 Reactive 데이터 처리
- 비동기 트랜잭션을 활용하여 성능 최적화
- AI 응답을 저장하여 데이터 분석 및 기록 보관 가능
### ✅ 5. React 기반 UI 및 마크다운 렌더링
- ReactMarkdown 을 활용하여 마크다운 변환 지원
- SSE(EventSource) 를 이용해 실시간 챗봇 UI 구현
- remark-gfm, rehype-raw 등을 사용하여 코드 블록 및 강조 기능 제공

---

## 📌 데이터 흐름
### ✅ 사용자 메시지 요청 → AI 응답 처리 흐름
```text
1️⃣ 사용자가 메시지를 입력하면 React UI → 백엔드 `/api/ai/stream` API 호출  
2️⃣ 백엔드는 WebClient 를 통해 Ollama AI 모델에 메시지 전송  
3️⃣ AI 모델이 스트리밍 형식으로 응답 생성 (JSON 데이터)  
4️⃣ 백엔드는 SSE(Server-Sent Events) 를 통해 응답을 클라이언트에 전달  
5️⃣ ReactMarkdown 을 활용해 마크다운 변환 후 화면에 표시  
```

---
## 📌 API 설계
### ✅ 1. AI 챗봇 메시지 전송 (스트리밍 응답)
- **Method**: GET
- **URL**: /api/ai/stream
- **Query Parameters**:
  - message : 사용자 입력 메시지

#### 요청 예시
```
GET /api/ai/stream?message=안녕하세요!
```
#### 응답 예시
```json
{
  "response": "안녕하세요! 무엇을 도와드릴까요?"
}
```

---

### ✅ 2. 챗봇 메시지 저장 (DB 저장)
- **Method**: POST
- **URL**: /api/ai/save
- Body:
```json
{
  "userId": "1234",
  "message": "한국은 어디에 있어?",
  "response": "한국은 동아시아에 위치한 나라입니다."
}
```
#### 응답 예시
```json
{
  "status": "success",
  "message": "대화 기록이 저장되었습니다."
}
```
