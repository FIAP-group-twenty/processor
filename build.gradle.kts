plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "hackaton"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

buildscript {
	repositories {
		maven("https://plugins.gradle.org/m2/")
	}
	dependencies {
		classpath("org.jlleitschuh.gradle:ktlint-gradle:11.5.1")
	}
}

repositories {
	mavenCentral()
}

val kotlinSdkVersion = "1.0.41"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("aws.sdk.kotlin:s3:$kotlinSdkVersion")
	implementation("aws.sdk.kotlin:s3control:$kotlinSdkVersion")
	implementation("aws.sdk.kotlin:sqs:1.2.28")
	implementation("com.arthenica:ffmpeg-kit-full:5.1")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
