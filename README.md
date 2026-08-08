# SwapClass Backend

> 숙대생들의 수강신청을 구조하는 간편하고 안전한 강의 교환 플랫폼.   
> 교환 매칭 및 강의 보유 인증 자동화.

---

## 프로젝트 소개

SwapClass는 수강신청 기간에 원하는 강의를 얻기 위해 서로 강의를 교환할 수 있는 서비스입니다.
게시글 등록 → 교환 요청 → 수락 → 채팅방 생성 → QR 인증 → 교환 완료의 흐름으로 안전한 거래를 지원합니다.

---

## 🛠 Tech Stack
![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-010101?style=flat&logo=socketdotio&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat&logo=amazonaws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat&logo=githubactions&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase_FCM-FFCA28?style=flat&logo=firebase&logoColor=black)


| 분류 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA |
| Database | MySQL |
| Cache | Redis |
| Auth | Spring Security · JWT |
| Realtime | WebSocket (STOMP) |
| Storage | AWS S3 |
| Infra | AWS EC2 · Nginx |
| CI/CD | GitHub Actions |
| Push | FCM (Firebase Cloud Messaging) |

---

## 📁 프로젝트 구조

```
src/main/java/com/sookmyung/swapclass/
├── domain/
│   ├── auth/           # 인증/회원가입/로그인
│   ├── user/           # 유저
│   ├── post/           # 게시글
│   ├── proposal/       # 교환 요청
│   ├── exchange/       # 교환
│   ├── chat/           # 채팅방/메시지
│   ├── verification/   # QR 인증
│   ├── notification/   # 알림
│   ├── push/           # FCM 푸시 알림
│   ├── report/         # 신고
│   ├── block/          # 차단
│   ├── inquiry/        # 문의
│   ├── graduation/     # 졸업요건 과목
│   ├── lounge/         # 라운지
│   ├── scheduler/      # 스케줄러
│   └── admin/          # 관리자
├── global/
│   ├── config/         # 설정 (Security, WebSocket, S3 등)
│   ├── jwt/            # JWT 필터/프로바이더
│   ├── exception/      # 전역 예외 처리
│   └── response/       # 공통 응답 포맷
└── infra/
    ├── s3/             # AWS S3 업로드
    ├── fcm/            # Firebase FCM 발송
    └── qr/             # ZXing QR 생성/디코딩
```

---

## 주요 기능

### 교환 채팅방 상태머신
```
CHATTING → SCHEDULED → VERIFYING → COUNTDOWN → DONE
                                  ↘ CANCELED
```

### QR 인증 흐름
```
QR 발급 (Redis 저장 10분) → 화면 공유 캡처 업로드 → QR 디코딩 → 검증 → 양측 완료 시 COUNTDOWN
```

### 스케줄러
| 스케줄러 | 주기 | 동작 |
|---|---|---|
| ProposalExpireJob | 1분 | 30분 내 미수락 요청 만료 |
| ChatTimeoutJob | 5분 | 교환 시간 30분 초과 무응답 자동 취소 |
| AutoConfirmJob | 1시간 | 교환 시간 72h 경과 자동 완료 |
| ExchangeAlarm30MinJob | 1분 | 교환 30분 전 알림 |
| ExchangeAlarm10MinJob | 1분 | 교환 10분 전 알림 |
| VerifyStartJob | 1분 | 교환 5분 전 인증 시작 + VERIFYING 전환 |

---

## 환경 변수 (env)

```env
DB_URL=jdbc:mysql://localhost:3306/swapclass
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
MAIL_USERNAME=
MAIL_PASSWORD=
AWS_ACCESS_KEY=
AWS_SECRET_KEY=
S3_BUCKET=
DISCORD_WEBHOOK_URL=
REDIS_HOST=localhost
REDIS_PORT=6379
```

---
## API 문서

- Swagger: https://swapclass.duckdns.org/swagger-ui/index.html

## 배포

- 서비스 링크: https://soo-frontend-git-develop-song-walks.vercel.app
---

## Role

| 이름 | 역할                                                 |
|---|----------------------------------------------------|
| 이지현 | BE Lead — 알림, 신고/차단, QR 인증, 교환 채팅방 상태머신, FCM, 스케줄러 |
| 이지민 | BE — 인증/회원, 게시글, 교환 요청, 마이페이지, 라운지                 |