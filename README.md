## 1 빌드 & 실행 방법

```bash
./gradlew clean build
./gradlew bootRun
```
> Swagger UI: [http://localhost:8080/doc](http://localhost:8080/doc) </br>
> OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

<br>

## 2 REST API 명세 요약

### 동기화 API

* **POST** `/api/countries/sync`
    * 특정 국가 동기화
  * **방법 : 스웨거에서 바로 execute 누르기**

* **POST** `/api/holidays/sync/all`
    * 최근 5년치 전체 국가의 공휴일 동기화 
    * **방법 : 스웨거에서 바로 execute 누르기**

* **POST** `/api/holidays/sync`
    * 특정 국가/연도의 공휴일만 동기화
    * **방법 : 스웨거에서 `{ "countryCode": "KR", "year": 2025 }` 바디 입력 후 execute 누르기**

### 공휴일 검색 및 조회 API

* **GET** `/api/holidays`
    * 국가별-기간별-공휴일타입별 필터, 키워드, 정렬, 페이징을 활용한 검색
    * **방법 : 스웨거에서 `countryCode: KR, from: 2025-01-03, to: 2025-01-02, page: 0, size: 10` 파라미터 입력 후 execute 누르기**

* **GET** `/api/holidays/{countryCode}/{date}`
    * 국가+날짜로 단일 공휴일 조회
    * **방법 : 스웨거에서 `countryCode: KR, date: 2025-10-03` 파라미터 입력 후 execute 누르기**

### 소프트 딜리트 API

* **DELETE** `/api/holidays/{countryCode}/{year}`
    * 특정 국가의 연도별 공휴일 일괄 삭제
    * **방법 : 스웨거에서 `countryCode: KR, date: 2025-01-01` 파라미터 입력 후 execute 누르기**

<br>

## 3 ./gradlew clean test 스크린샷

<img width="730" alt="스크린샷 2025-07-02 오전 7 00 11" src="https://github.com/user-attachments/assets/4bc6ff95-6868-4cbb-8609-a7bea7c7a5e3" />

<br> <br>

## 4 구현 포인트 

### 1. **국가 & 공휴일 도메인 설계**

* `Country`, `Holiday` 엔티티 분리.
* 완전 정규화 대신 국가와 조인 제거하여 속도 향상과 배치 시 간결한 데이터 처리 
* 응답 형태가 정해진 외부 api를 통해 데이터를 저장하므로 조인없이 countryCode를 필드로 가짐 (독립된 테이블 유지)
* 복합 인덱스를 통한 조회 성능 및 정렬 성능 최적화 
* 빠른 처리 속도를 위해 JPQL 직접 업데이트로 대량 소프트 삭제 구현

### 2. **WebClient + CompletableFuture 통한 비동기 병렬 처리**

* 외부 API로부터 국가별 공휴일을 병렬로 조회하여 배치 처리 속도 대폭 개선.
* `holidayExecutor` 쓰레드 풀 구성:

### 3. **스케줄러 기반 자동 동기화 배치**

* 매년 1월 2일 오전 1시에 전 국가 공휴일 동기화
* `@Scheduled` + `CompletableFuture.allOf()` + `ThreadPoolTaskExecutor` 를 사용한 병렬 배치
* 국가별 처리 중 오류 발생 시 로깅만 하고 전체 배치는 계속 수행

### 4. **Mapper + ObjectMapper 기반 JSON 필드 처리**

* 공휴일 API에서 내려온 `countries`, `types` 는 단순한 속성 목록으로 저장 시  List<String> → JSON 문자열 직렬화, 응답 시 JSON 문자열 → List<String> 역직렬화를 통해 입출력 처리 단순화

### 5. **QueryDSL 기반 Holiday 검색 기능 구현과 Custom Pagination 설계**

* 복합 필터 (`countryCode`, 날짜 범위, 키워드, type)
* 다중 정렬 조건 대응을 위한 `OrderSpecifier` 동적 생성
* JPA의 Pageable 의존 없이 offset, limit, totalCount를 직접 제어하는 Pagination<T> 도입
* 클라이언트 요구에 맞춰 커스텀 정렬 조건(`최신순`, `국가순`) 처리.

### 6. **커스텀 예외 처리 구조 적용**

* `HolidayErrorCode`, `CustomException`, `GlobalExceptionHandler`

<img width="1093" alt="image" src="https://github.com/user-attachments/assets/b1112404-e45d-41ee-9d6a-b82f6c2f4b12" />

<br> <br>

## 5 커밋 로그 요약

* `[Test] 테스트 코드 구현`
* `[feat] 컨트롤러 구현 및 스웨거 적용`
* `[feat] 배치 작업 구현 - 병렬 처리, Executor, 로그 처리, 중도 실패 허용`
* `[feat] 커스텀 에러 처리 및 예외 핸들링 적용`
* `[feat] 비지니스 로직 + DTO 적용 - 국가/공휴일 분리 설계`
* `[feat] mapper 구현 - 외부 DTO 직렬화`
* `[feat] 외부 API 호출 구현 - WebClient 기반 처리`
* `[feat] 커스텀 페이지네이션 구현 - JPA 의존 제거`
* `[feat] 리포지토리 구현 - QueryDSL, DIP 설계`
* `[feat] 도메인 구현 - 복합 인덱스, 정규화 제거`
