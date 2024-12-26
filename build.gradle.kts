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
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // Basic
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // DB connector
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("mysql:mysql-connector-java:8.0.32") // '8.0.40'으로 맞추면 에러...

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // JWT
    implementation ("io.jsonwebtoken:jjwt:0.9.1") // Java JWT library
    implementation ("javax.xml.bind:jaxb-api:2.3.1") // XML document와 Java 객체 간 매핑 자동화

    // BCrypt Encoder (Spring Security 의존성 추가 없이 Encoder 사용할 때)
    implementation ("at.favre.lib:bcrypt:0.10.2")

    // Swagger
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
