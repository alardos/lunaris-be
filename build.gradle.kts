plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.liquibase.gradle") version "3.0.2"
}

group = "com.alardos"
version = "0.0.1-SNAPSHOT"
buildscript {
	dependencies {
		classpath("org.postgresql:postgresql:42.7.5")
		classpath("org.liquibase:liquibase-core:4.31.1")
		classpath("org.liquibase.ext:liquibase-postgresql:4.31.1")
	}
}
liquibase {
	runList = "main"
	activities.register("main") {
		this.arguments = mapOf(
			"searchPath" to "src/main/resources",
			"changelogFile" to "db/changelog.xml",
			"url" to "jdbc:postgresql://"+System.getenv("DB_HOST")+":"+System.getenv("DB_PORT")+"/"+System.getenv("DB_NAME"),
			"username" to System.getenv("DB_USER"),
			"password" to System.getenv("DB_PASSWORD"),
			"driver" to "org.postgresql.Driver",
			"count" to 1,
		)
	}

}
java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	maven { url = uri("https://jitpack.io") }
	mavenCentral()
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

	implementation("org.springframework.boot:spring-boot-starter-jdbc:3.4.3")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.security:spring-security-crypto:6.4.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.jdbi:jdbi3-kotlin:3.48.0")
	testImplementation("org.jdbi:jdbi3-testing:3.48.0")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

	testImplementation("org.testcontainers:testcontainers:1.20.6")
	testImplementation("org.testcontainers:junit-jupiter:1.20.6")
	testImplementation("org.testcontainers:postgresql:1.20.6")
	testImplementation("org.springframework.boot:spring-boot-testcontainers:3.4.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("org.liquibase:liquibase-core:4.31.1") // Use the appropriate version
	liquibaseRuntime("org.liquibase:liquibase-core:4.31.1") // Use the appropriate version
	implementation("org.postgresql:postgresql:42.7.5") // Ensure this matches your PostgreSQL JDBC driver version
	liquibaseRuntime("org.postgresql:postgresql:42.7.5") // Ensure this matches your PostgreSQL JDBC driver version
	liquibaseRuntime("info.picocli:picocli:4.7.6") // Add Picocli as a runtime dependency

	implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.1")
}


tasks.withType<Test> {
	useJUnitPlatform()
}
