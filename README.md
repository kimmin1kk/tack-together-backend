# Tack-Together Back-end

<p align = left> <img width="150" alt="App Icon" src="https://user-images.githubusercontent.com/101302590/232203145-b179c27f-2062-45ba-a31c-7bb807d50807.png" /> </p>

### 이 프로젝트는 택시 동승자 매칭을 위한 안드로이드 어플리케이션 "택 투게더" 백엔드 서버입니다.

## Contributor
[jeongmu1](https://github.com/jeongmu1)

[kimmin1kk](https://github.com/kimmin1kk)

[alfn051](https://github.com/alfn051)

## Info

#### 택시 비용을 절감하기 위한 동승자 매칭 시스템으로, 여러 사용자가 같은 택시를 이용할 경우 택시 비용을 절감할 수 있습니다.

#### 택시를 이용하기 전 출발지 범위와 목적지 범위 조건에 부합한 사용자끼리 매칭되어 함께 택시를 이용할 수 있습니다.

#### 범위 레벨 별 거리는 아래와 같습니다.

| 범위 레벨 | 출발지 범위 | 목적지 범위 |
| --------- | ----------- | ----------- |
| 좁게      | 100m        | 500m        |
| 보통      | 250m        | 1km         |
| 넓게      | 500m        | 2km         |

#### 또한, 사용자들은 매칭 정보 및 매칭된 동승자의 실시간 위치 정보(웹소켓 사용한 실시간 통신)를 확인하여 수월하게 택시를 이용할 수 있습니다.

#### 택투게더는 편리한 택시 이용을 위해 더 나은 선택이 될 것입니다.

## Requirements
+ JDK 11
+ MySQL 8.0 이상
+ Maven
+ Redis

## Settings

프로젝트를 실행하기 전에 `src/main/resources` 디렉토리에 `application.yml` 파일을 생성하고 아래와 같은 내용을 작성해주세요.
```yaml
app:
  redis:
    ttl: 60

spring:
  redis:
    host: 127.0.0.1
    port: 6379

  datasource:
    url: jdbc:mysql://localhost:3306/tack_together?useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=true
    driver-class-name: com.mysql.cj.jdbc.Driver
    password: [데이터베이스 비밀번호]
    username: [데이터베이스 사용자명]

  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        hbm2ddl:
          auto: create-drop
    open-in-view: false

jwt:
  header: Authorization
  secret: [JWT 시크릿 키]
  access-token-expiration: 3600000
  refresh-token-expiration: 1209600000

api:
  kakao:
    key: [카카오 API 키]
    mobility-base-url: https://apis-navi.kakaomobility.com/v1/

match:
  range:
    origin:
      narrow: 100
      normal: 250
      wide: 500
    destination:
      narrow: 500
      normal: 1000
      wide: 2000
fare:
  minimum:
    distance: 2
    fare: 3800
```

## Tech Stack

+ Java 11
+ Spring Boot 2.2.6
+ Spring Data JPA
+ MySQL
+ hibernate 5.4.24.Final
+ spring-security-jwt 1.1.1
+ Redis
+ jackson.core 2.10.3
+ geodesy 1.1.3
+ Lombok 1.18.12
+ jUnit5 5.5.2
+ Maven
