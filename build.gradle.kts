import java.util.*

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val springSecurityVersion: String by project
val postgresVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")

    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.3")
}

tasks.test {
    useJUnitPlatform()
}

val props = Properties()
props.load(file("src/main/resources/db/liquibase.properties").inputStream())

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "changeLogFile" to props["change-log-file"],
            "url" to props["url"],
            "username" to props["username"],
            "password" to props["password"],
            "driver" to props["driver-class-name"]
        )
    }
}