import com.github.jk1.license.filter.SpdxLicenseBundleNormalizer
import com.github.jk1.license.render.XmlReportRenderer
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.util.*

plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "7.2.3.7755"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.openapi.generator") version "7.20.0"
    id("org.ajoberstar.grgit") version "5.3.2"
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
    id("com.github.jk1.dependency-license-report") version "3.1.1"
}

group = "it.gov.pagopa"
version = "0.0.1"
description = "arpu-be"

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

licenseReport {
    renderers = arrayOf(XmlReportRenderer("third-party-libs.xml", "Back-End Libraries"))
    outputDir = "$projectDir/dependency-licenses"
    filters = arrayOf(SpdxLicenseBundleNormalizer())
}
tasks.classes {
    finalizedBy(tasks.generateLicenseReport)
}

repositories {
    mavenCentral()
}

val springDocOpenApiVersion = "3.0.2"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.9"
val wiremockVersion = "3.13.2"
val javaJwtVersion = "4.5.1"
val jwksRsaVersion = "0.23.0"
val mapStructVersion = "1.6.3"
val micrometerVersion = "1.6.3"
val commonsLang3Version = "3.20.0"
val commonsFileUploadVersion = "1.6.0"
val httpClientVersion = "5.6"
val httpCoreVersion = "5.4.1"
val podamVersion = "8.0.2.RELEASE"
val bouncycastleVersion = "1.83"

// fix cve
val jackson2CoreVersion = "2.21.1"
val jackson3CoreVersion = "3.1.0"

val springCloudDepsVersion = "2025.1.1"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudDepsVersion")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.httpcomponents.client5:httpclient5:${httpClientVersion}")
    implementation("org.apache.httpcomponents.core5:httpcore5:${httpCoreVersion}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
    implementation("org.codehaus.janino:janino:$janinoVersion")
    implementation ("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")
    implementation("commons-fileupload:commons-fileupload:$commonsFileUploadVersion")
    implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
    implementation("org.mapstruct:mapstruct:${mapStructVersion}")
    // validation token jwt
    implementation("com.auth0:java-jwt:${javaJwtVersion}")
    implementation("com.auth0:jwks-rsa:${jwksRsaVersion}")

    // CVE fix
    implementation("tools.jackson.core:jackson-core:${jackson3CoreVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jackson2CoreVersion}")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")
    testAnnotationProcessor("org.projectlombok:lombok")

    //	Testing
    testCompileOnly("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
    testImplementation("uk.co.jemos.podam:podam:${podamVersion}")
    testImplementation("org.projectlombok:lombok")
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
        testLogging.events = setOf(TestLogEvent.FAILED)
        testLogging.exceptionFormat = TestExceptionFormat.FULL
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
    dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
    group = "AutomaticallyGeneratedCode"
    description = "grouping all together automatically generate code tasks"

    dependsOn(
        "openApiGenerate",
        "openApiGenerateP4PAAUTH",
        "openApiGenerateP4PACITIZEN",
        "openApiGenerateGOOGLERECAPTCHA"
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
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "requestMappingMode" to "api_interface",
            "useSpringBoot3" to "true",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useBeanValidation" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "additionalModelTypeAnnotations" to "@lombok.Builder"
        )
    )
    typeMappings.set(
        mapOf(
            "DateTime" to "java.time.OffsetDateTime",
            "zoned-date-time" to "java.time.ZonedDateTime",
            "OrganizationsWithSpontaneousDTO" to "it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO",
            "DebtPositionTypeOrgsWithSpontaneousDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO",
            "DebtPositionTypeOrgsWithSpontaneousDetailsDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO",
            "DebtPositionRequestDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO",
            "DebtPositionResponseDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO",
            "DebtPositionDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO",
            "PagedDebtorReceiptsDTO" to "it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO",
            "ReceiptDetailExtendedDTO" to "it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO",
            "BrokerInfoDTO" to "it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO",
            "PagedDebtorDebtPositionDTO" to "it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorDebtPositionDTO",
            "DebtorUnpaidDebtPositionOverviewDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionOverviewDTO",
            "InstallmentDebtorExtendedDTO" to "it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO",
            "DebtorUnpaidDebtPositionInstallmentsDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO",
            "DebtorReceiptDTO" to "it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO",
            "InstallmentStatus" to "it.gov.pagopa.pu.citizen.dto.generated.InstallmentStatus"

        )
    )
}

var targetEnv = when (Objects.requireNonNullElse(System.getProperty("targetBranch"), grgit.branch.current().name)) {
    "uat" -> "uat"
    "main" -> "main"
    else -> "develop"
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateP4PAAUTH") {
    group = "openapi"
    description = "openapi"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-auth/refs/heads/$targetEnv/openapi/p4pa-auth.openapi.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.pu.auth.controller.generated")
    modelPackage.set("it.gov.pagopa.pu.auth.dto.generated")
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    library.set("resttemplate")
}


tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateP4PACITIZEN") {
    group = "openapi"
    description = "openapi"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-citizen/refs/heads/$targetEnv/openapi/generated.openapi.json")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.pu.citizen.controller.generated")
    modelPackage.set("it.gov.pagopa.pu.citizen.dto.generated")
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    typeMappings.set(
        mapOf(
            "string+binary" to "Resource"
        )
    )
    importMappings.set(
        mapOf(
            "Resource" to "org.springframework.core.io.Resource"
        )
    )
    library.set("resttemplate")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateGOOGLERECAPTCHA") {
    group = "openapi"
    description = "openapi"

    generatorName.set("java")
    inputSpec.set("$rootDir/openapi/external/google-recaptcha.openapi.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.google.recaptcha.controller.generated")
    modelPackage.set("it.gov.pagopa.google.recaptcha.dto.generated")
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    library.set("resttemplate")
}
