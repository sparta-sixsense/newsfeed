plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.sixsense"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    // QueryDSL 전용 configuration 정의
    val querydsl by creating {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Basic
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta") // Querydsl의 JPA 관련 기능 제공 (JPAQueryFactory, BooleanBuilder, PathBuilder ...)
    annotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta") // QueryDSL APT(Annotation Processing Tool)로 컴파일 시점에 @Entity 애노테이션이 붙은 JPA 엔티티 클래스를 기반으로 Q 클래스를 생성
    annotationProcessor("jakarta.annotation:jakarta.annotation-api") // QueryDSL APT에서 사용하는 @Generated 애노테이션을 제공.  클래스 생성 시 @Generated 애노테이션이 필요하며, 이를 처리하기 위해 이 의존성이 필요
    annotationProcessor("jakarta.persistence:jakarta.persistence-api") // JPA 표준 애노테이션(@Entity, @Id, @Column 등)을 정의. QueryDSL APT가 엔티티 클래스를 분석하고 Q 클래스를 생성하는 데 필수

    testImplementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    testAnnotationProcessor("com.querydsl:querydsl-apt:5.1.0:jakarta")
    testAnnotationProcessor("jakarta.annotation:jakarta.annotation-api")
    testAnnotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // DB connector
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("mysql:mysql-connector-java:8.0.32")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // JWT
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // BCrypt Encoder
    implementation("at.favre.lib:bcrypt:0.10.2")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// QueryDSL로 생성된 Q 클래스 출력 dir 설정
tasks.named<JavaCompile>("compileJava") {
    options.generatedSourceOutputDirectory.set(layout.buildDirectory.dir("generated/querydsl"))
}

// QueryDSL로 생성된 소스 directory를 Java 소스 경로로 추가
// QueryDSL에서 생성된 Q 클래스를 컴파일 시 자동으로 포함되게 설정
sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("/generated/querydsl"))
    }
}

// JUnit 5를 테스트 플랫폼으로 설정
tasks.withType<Test> {
    useJUnitPlatform()
}