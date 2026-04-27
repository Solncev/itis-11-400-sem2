import java.util.*

plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.liquibase.gradle") version "2.2.2"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val springSecurityVersion: String by project
val postgresVersion: String by project
val jwtVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("org.springframework.security:spring-security-taglibs:${springSecurityVersion}")

    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.3")

    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jwtVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
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

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

val jacocoExcludes = listOf(
    "**/com/solncev/dto/**",
    "**/com/solncev/model/**",
    "**/com/solncev/config/**"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.1)
            }
        }
    }

    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}