# 📖덕후감 API

## 프로젝트 개요

덕후감은 도서를 검색하고 리뷰를 작성하며, 리뷰에 댓글과 좋아요를 남길 수 있는 도서 리뷰 서비스입니다.

본 저장소는 덕후감 서비스의 API 서버를 담당합니다.

API 서버는 사용자, 도서, 리뷰, 댓글, 알림, 대시보드 조회 기능을 제공합니다.

### 프로젝트 목표

- PostgreSQL과 MongoDB를 활용한 도메인 데이터 및 대시보드 데이터 저장 구조 설계
- 사용자, 도서, 리뷰, 댓글, 알림 등 주요 도메인 API 구현
- 인기 도서, 인기 리뷰, 파워 유저 등 대시보드 조회 API 제공
- API 서버와 Batch 서버를 분리한 Multi Repository 구조 설계
- Bean Validation, 커스텀 예외, 테스트 코드를 통한 API 안정성 확보

---

## 팀원 구성 및 역할 분담

| 역할   | 담당자 | 담당 도메인   |
| ------ | ------ | ------------- |
| 👑팀장 | 이예진 | 알림 관리     |
| 🧑‍💻팀원 | 배성준 | 도서 관리     |
| 🧑‍💻팀원 | 이경훈 | 리뷰 관리     |
| 🧑‍💻팀원 | 안소현 | 댓글 관리     |
| 🧑‍💻팀원 | 이주혜 | 사용자 관리   |
| 🧑‍💻팀원 | 유예성 | 대시보드 관리 |

## 주요 기능

- **사용자 관리**: 회원가입, 로그인, 사용자 정보 조회, 닉네임 수정, 회원 삭제
- **도서 관리**: 도서 등록, 목록 조회, 상세 조회, 수정 및 삭제, OCR 기반 ISBN 추출
- **리뷰 관리**: 리뷰 등록, 목록 조회, 상세 조회, 수정 및 삭제, 리뷰 좋아요
- **댓글 관리**: 댓글 등록, 목록 조회, 상세 조회, 수정 및 삭제
- **알림 관리**: 알림 목록 조회, 읽음 처리, 전체 읽음 처리, 삭제
- **대시보드**: 인기 도서, 인기 리뷰, 파워 유저, 사용자 활동 통계 조회

---

## 기술 스택

### Backend

<img src="https://img.shields.io/badge/Java_17-007396?style=for-the-badge&logo=openjdk&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">

### Database

<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white">

### DevOps & Tools

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"> <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">

---

## Repository 구조

덕후감 프로젝트는 API 서버와 Batch 서버를 분리한 Multi Repository 구조로 구성합니다.

- **`deokhugam-api`**
  - 사용자, 도서, 리뷰, 댓글, 알림, 대시보드 조회 API 담당
- **`deokhugam-batch`**
  - 인기 도서 계산
  - 인기 리뷰 계산
  - 파워 유저 계산
  - 읽은 알림 자동 삭제
  - 삭제 대상 회원 물리 삭제

API 서버와 Batch 서버를 분리하여 배치 작업이 API 서버의 사용자 요청 처리에 직접 영향을 주지 않도록 설계합니다.

## 도메인 기반 패키지 구조

덕후감 API 서버는 도메인별 책임을 분리하기 위해 도메인 기반 패키지 구조를 사용합니다.

```
src/main/java/com/sbproject/deokhugam
├── user
├── book
├── review
├── comments
├── notification
├── dashboard
├── common
└── config
```

각 도메인은 기능에 따라 Controller, Service, Repository, Entity, DTO, Exception 계층으로 구성합니다.

```
domain
├── controller
├── dto
├── entity
├── exception
├── repository
└── service
```

- 대시보드 도메인은 MongoDB를 사용하기 때문에 MongoDB 컬렉션과 매핑되는 `document` 패키지를 별도로 사용합니다.
- 공통 응답, 공통 예외, 공통 엔티티 등 여러 도메인에서 함께 사용하는 코드는 `common` 패키지에서 관리합니다.

## 데이터베이스 설계

덕후감은 데이터 성격에 따라 PostgreSQL과 MongoDB를 함께 사용합니다.

### PostgreSQL

정합성이 중요한 핵심 도메인 데이터를 저장합니다.

- 사용자
- 도서
- 리뷰
- 댓글
- 알림
- 좋아요

### MongoDB

대시보드 조회용 랭킹/통계 데이터를 저장합니다.

- 인기 도서
- 인기 리뷰
- 파워 유저

---

## Git / GitHub 전략

<img width="1671" height="930" alt="image" src="https://github.com/user-attachments/assets/d39b5659-bbf6-41fa-887e-867d36069643" />

- 기능 개발은 각자 담당 도메인의 `feature` 브랜치에서 진행
- Pull Request를 통해 변경 사항을 공유하고 코드 리뷰를 진행
- 공통 코드나 다른 도메인에 영향을 줄 수 있는 변경 사항은 팀원과 먼저 공유
- merge 전 최신 `main` 브랜치를 반영하여 충돌을 최소화
