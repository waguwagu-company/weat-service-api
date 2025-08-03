# 위잇 (Weat) 

## 📖 Description
**위잇(Weat)**은 *We* + *Eat*의 합성어로 함께 먹는다는 의미를 담은 **AI 기반 모임 위치 및 리뷰 기반 맛집 추천 서비스**입니다. 

이 레포지토리는 Spring Boot 기반 백엔드 서비스로, 사용자 및 그룹 관리, 선호도 처리, AI 서비스와의 통신 등 비즈니스 로직을 담당합니다.
AI 연산 및 분석 로직은 별도의 Python 기반 서비스에서 처리됩니다.

**Weat**, a combination of *We* and *Eat*, is an AI-powered platform that recommends restaurants and meeting points based on group preferences, locations, and reviews.

This repository is the Spring Boot-based backend service responsible for handling business logic such as user and group management, preference handling, and communication with the AI service.
The AI computation and analysis logic are implemented in a separate Python-based service.


## 🚀 Features
- 그룹 생성, 참여, UUID 기반 세션 링크 관리 (Manage group creation, participation, and UUID-based session links)
- 사용자 위치 데이터 수집 (Collect user location data)
- 사용자 선호도(호/불호) 수집 (Gather user preferences (likes/dislikes))
- 사용자들의 비정형적 요구사항 수집 (Handle unstructured user requirement)
- AI 서비스와 통신하여 추천 결과를 받아 프론트엔드에 반환 (Communicate with the AI service to retrieve recommendations and return them to the frontend)


## 🛠 Tech Stack
- Java 17
- Spring Boot 3.5.4
- Gradle
- PostgreSQL


## 📂 Project Structure
```
project-root/
 ├─ src/
 │   ├─ main/java
 │   ├─ main/resources
 ├─ build.gradle
 └─ README.md
```
