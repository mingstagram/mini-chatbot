# 🤖 AI 챗봇 프로젝트
**Spring Boot WebFlux | Ollama | Redis | JUnit | React**

## 📌 프로젝트 개요
이 프로젝트는 **Spring WebFlux + Ollama + Redis**를 활용한 **비동기 AI 챗봇** 애플리케이션입니다.  
**WebFlux 비동기 처리**를 기반으로 한 **AI 모델 응답 스트리밍**을 구현하고, **Redis**를 활용하여 **세션 데이터**를 최적화하며, **React 기반 프론트엔드**를 통해 사용자 친화적인 인터페이스를 제공합니다.

### ✅ 주요 목표:
- **비동기 아키텍처**를 활용한 **실시간 AI 챗봇** 구현
- **Ollama API**를 활용한 **AI 모델 연동 및 스트리밍 응답**
- **Redis 기반 세션 저장 및 캐싱**
- **WebFlux 기반 비동기 API 설계 및 최적화**
- **React 기반 UI 및 마크다운 변환 지원**

---

## 📌 기술 스택
| 카테고리 | 기술 |
|----------|--------------------------------|
| **Backend** | Spring Boot (WebFlux, R2DBC) |
| **AI 모델** | Ollama API (phi-4) |
| **Database** | MariaDB (Reactive R2DBC) |
| **Cache & Session** | Redis | 
| **Frontend** | React, ReactMarkdown, WebSockets (SSE) |
| **Build Tool** | Maven |
| **Containerization** | Docker & Docker Compose |

---

## 📌 주요 기능
### ✅ 1. Ollama AI 모델 연동 및 스트리밍 응답
- **WebClient 기반 Ollama API 연동**
- **Server-Sent Events (SSE)** 방식으로 AI 응답 실시간 스트리밍
- **마크다운 응답 처리 (ReactMarkdown)**

### ✅ 2. WebFlux 기반 비동기 API
- **Spring WebFlux** 기반 비동기 데이터 처리
- **비동기 Ollama API 호출 및 스트리밍 데이터 전송** 

### ✅ 3. React 기반 UI 및 마크다운 지원
- **ReactMarkdown을 활용한 AI 응답 렌더링**
- **SSE(EventSource) 활용하여 실시간 채팅 지원**
- **코드 하이라이팅 및 마크다운 지원**

### ✅ 4. Redis 기반 세션 관리 및 캐싱
- **사용자별 AI 대화 기록 저장**
- **빠른 응답을 위한 Redis 캐싱 활용**