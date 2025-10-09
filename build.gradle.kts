plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	jacoco
    id("org.sonarqube") version "6.3.1.5724"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.openapi.generator") version "7.15.0"
    id("com.gorylenko.gradle-git-properties") version "2.5.3"
}

group = "it.gov.pagopa"
version = "0.0.1"

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

repositories {
	mavenCentral()
}

val springDocOpenApiVersion = "2.8.13"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.7"
val wiremockVersion = "3.13.1"
val javaJwtVersion = "4.5.0"
val jwksRsaVersion = "0.23.0"
val mapStructVersion = "1.6.3"
val micrometerVersion = "1.5.4"
val commonsLang3Version = "3.19.0"
val commonsFileUploadVersion = "1.6.0"

val springCloudDepsVersion = "2025.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudDepsVersion")
    }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign") {
        exclude(group = "commons-fileupload", module = "commons-fileupload")
    }
    implementation("commons-fileupload:commons-fileupload:$commonsFileUploadVersion")
	implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
    implementation("org.mapstruct:mapstruct:${mapStructVersion}")
    // validation token jwt
    implementation("com.auth0:java-jwt:${javaJwtVersion}")
    implementation("com.auth0:jwks-rsa:${jwksRsaVersion}")

	compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")


	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.wiremock:wiremock-standalone:$wiremockVersion")
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
	mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
	test {
		jvmArgs("-javaagent:${mockitoAgent.asPath}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
	}
}

val projectInfo = mapOf(
		"artifactId" to project.name,
		"version" to project.version
)

tasks {
	val processResources by getting(ProcessResources::class) {
		filesMatching("**/application.yml") {
			expand(projectInfo)
		}
	}
}

tasks.compileJava {
	dependsOn("openApiGenerate")
}

tasks.register("dependenciesBuild") {
	group = "AutomaticallyGeneratedCode"
	description = "grouping all together automatically generate code tasks"

	dependsOn(
		"openApiGenerate"

	)
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

springBoot {
    buildInfo()
	mainClass.value("it.gov.pagopa.arc.PagopaArcBeApplication")
}

openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$rootDir/openapi/pagopa-arc-be.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	apiPackage.set("it.gov.pagopa.arc.controller.generated")
	modelPackage.set("it.gov.pagopa.arc.model.generated")
	configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "requestMappingMode" to "api_interface",
        "useSpringBoot3" to "true",
        "interfaceOnly" to "true",
        "useTags" to "true",
        "useBeanValidation" to "true",
        "generateConstructorWithAllArgs" to "true",
        "generatedConstructorWithRequiredArgs" to "true",
        "additionalModelTypeAnnotations" to "@lombok.Builder"
	))
	typeMappings.set(mapOf(
        "DateTime" to "java.time.LocalDateTime",
        "zoned-date-time" to "java.time.ZonedDateTime"
	))
}
