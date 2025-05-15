plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.alxsshv"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.postgresql:postgresql:42.7.5")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
	implementation("io.jsonwebtoken:jjwt:0.12.6")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
	implementation("org.liquibase:liquibase-core:4.31.1")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	compileOnly ("org.mapstruct:mapstruct:1.6.0")
	annotationProcessor ("org.mapstruct:mapstruct-processor:1.6.0")
	annotationProcessor ("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation ("org.testcontainers:junit-jupiter:1.21.0")
	testImplementation ("org.testcontainers:postgresql:1.21.0")
	testImplementation("org.awaitility:awaitility:4.3.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
