plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.sonarqube") version "6.0.1.5171"
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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("aws.sdk.kotlin:s3:$kotlinSdkVersion")
	implementation("aws.sdk.kotlin:s3control:$kotlinSdkVersion")
	implementation("aws.sdk.kotlin:sqs:1.2.28")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.8.9")
	implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
	implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.1.0"))

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("io.mockk:mockk:1.13.3")
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
	testImplementation("org.mockito:mockito-core:5.2.0")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
		jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
	}
}

sonarqube {
	properties {
		property ("sonar.projectKey", "group-twenty_processor")
		property ("sonar.organization", "group-twenty")
		property ("sonar.host.url", project.findProperty("SONAR_HOST_URL") ?: "")
		property("sonar.login", project.findProperty("SONAR_TOKEN") ?: "")
		property("sonar.kotlin.language.level", "1.9")
		property("sonar.sources", "src/main/kotlin")
		property("sonar.tests", "src/test/kotlin")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
