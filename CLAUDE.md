# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# 로컬 인프라 실행 (PostgreSQL + Redis)
docker-compose up -d

# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 단일 테스트 클래스 실행
./gradlew test --tests "com.jobhelper.careerflowapi.TargetTest"

# 애플리케이션 실행 (local 프로파일 기본 적용)
./gradlew bootRun
```

## 환경 변수

`.env` 파일에 다음 항목이 필요하다:

```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
CORS_ALLOWED_ORIGINS=
POSTGRES_DB=
POSTGRES_USER=
POSTGRES_PASSWORD=
DB_PORT=
```

## 아키텍처 개요

**Spring Boot 4.0.5 / Java 25** 기반 REST API.

### 레이어 구조 (`user` 도메인 기준)

```
presentation/controller  - HTTP 진입점, @RequestBody 처리
    ↓
command/                 - AccountCommandHandler: 쓰기 커맨드 오케스트레이션
application/             - AuthSessionService: 토큰 갱신·로그아웃 등 세션 로직
    ↓
strategy/                - EmailAccountStrategy / SocialAccountStrategy: 인증 방식별 전략
    ↓
domain/entity            - User, UserAccount (JPA 엔티티)
infrastructure/          - Spring Data JPA Repository, Redis (AuthSessionRepository)
```

**Docs 인터페이스 패턴**: 컨트롤러는 `AuthControllerDocs` 인터페이스를 구현한다. Swagger 어노테이션(`@Operation`, `@Tag`, 에러 응답 등)은 모두 인터페이스에, 실제 로직은 컨트롤러에 분리되어 있다. 에러 응답 어노테이션은 `docs/error/` 하위의 커스텀 `@interface`로 조합한다.

### 인증 흐름

- **JWT + RTR(Refresh Token Rotation)**: 액세스 토큰은 Bearer 헤더, 리프레시 토큰은 `httpOnly` 쿠키로 관리.
- 리프레시 토큰은 Redis(`AuthSessionRepository`)에 저장. 재사용 감지 시 세션 전체 삭제(`REFRESH_TOKEN_THEFT_DETECTED`).
- `SecurityFacade` / `SecurityFacadeImpl`이 토큰 발급·검증·쿠키 생성을 추상화한다.
- `JwtAuthenticationFilter`가 요청마다 토큰을 검증하고 `SecurityContext`에 `PrincipalDetails`를 설정한다.

### API 버전 관리

`X-API-Version` 요청 헤더로 버전을 지정한다. 기본값 `1.0.0`. (`ApiVersionConfig`)

### 공통 응답 포맷

모든 응답은 `CommonResponse<T>` 래퍼를 사용한다:

```json
{ "result": "SUCCESS|FAIL", "code": "200|U001|...", "message": "...", "data": {}, "timestamp": "..." }
```

에러 코드 체계: `G-xxx`(글로벌), `U-xxx`(유저), `A-xxx`(인증/JWT/RTR). `ErrorCode` enum에 정의.

### 이벤트 기반 사이드이펙트

`UserEventHandler`, `AuthEventHandler`가 `@TransactionalEventListener` + `@Async`로 도메인 이벤트를 처리한다. 현재 미구현 TODO:
- 회원가입 후 웰컴 이메일 전송
- 로그인 실패 연속 카운터 및 계정 잠금
- 소셜 계정 연동 (`AccountLinkingService`)
- OAuth2 플로우 (`SocialAccountStrategy`)

### Swagger

`http://localhost:8080/swagger-ui/index.html` (local 환경에서만 활성화)