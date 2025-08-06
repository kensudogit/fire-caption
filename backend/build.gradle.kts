import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.sonarqube") version "4.4.1.3373"
    id("jacoco") version "0.8.11"
    id("checkstyle") version "10.12.5"
    id("com.github.spotbugs") version "5.2.1"
    id("org.flywaydb") version "10.8.1"
    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "com.firecaptain"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    testCompileOnly {
        extendsFrom(configurations.testAnnotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

extra.apply {
    set("mapstructVersion", "1.5.5.Final")
    set("lombokVersion", "1.18.30")
    set("jjwtVersion", "0.12.3")
    set("postgresqlVersion", "42.7.1")
    set("ehcacheVersion", "3.10.8")
    set("testcontainersVersion", "1.19.3")
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Database
    implementation("org.postgresql:postgresql:${property("postgresqlVersion")}")
    implementation("org.flywaydb:flyway-core")
    
    // Cache
    implementation("org.ehcache:ehcache:${property("ehcacheVersion")}")
    implementation("javax.cache:cache-api")
    
    // JWT
    implementation("io.jsonwebtoken:jjwt-api:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jjwtVersion")}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jjwtVersion")}")
    
    // Mapping
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    
    // Utilities
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.h2database:h2")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testCompileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    testAnnotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// MapStruct configuration
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.verbose=true"
    ))
}

// Checkstyle configuration
checkstyle {
    toolVersion = "10.12.5"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    configProperties["baseDir"] = rootDir
}

// SpotBugs configuration
spotbugs {
    effort.set(com.github.spotbugs.snom.spotbugs.gradle.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.spotbugs.gradle.ReportLevel.MEDIUM)
    excludeFilter.set(file("${rootDir}/config/spotbugs/exclude.xml"))
}

spotbugsMain {
    reports {
        create("html") {
            required.set(true)
            outputLocation.set(file("$buildDir/reports/spotbugs/main/spotbugs.html"))
            stylesheet.set("fancy-hist.xsl")
        }
    }
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
        rule {
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

// SonarQube configuration
sonarqube {
    properties {
        property("sonar.projectKey", "fire-captain-backend")
        property("sonar.projectName", "Fire Captain Backend")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.java.source", "17")
        property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

// Flyway configuration
flyway {
    url = "jdbc:postgresql://localhost:5432/fire_captain_db"
    user = "fire_captain_user"
    password = "fire_captain_password"
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}

// Version catalog for dependency management
dependencyLocking {
    lockAllConfigurations()
}

// Custom tasks
tasks.register("printVersion") {
    doLast {
        println("Fire Captain Backend Version: ${version}")
        println("Java Version: ${System.getProperty("java.version")}")
        println("Gradle Version: ${gradle.gradleVersion}")
    }
}

tasks.register<JavaExec>("generateApiDocs") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.firecaptain.OpenApiGenerator")
    args("--output", "${buildDir}/docs/api")
}

// Build optimization
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    forkEvery = 100L
}

// Docker build task
tasks.register<Exec>("dockerBuild") {
    dependsOn(tasks.bootJar)
    commandLine("docker", "build", "-t", "fire-captain-backend:${version}", ".")
}

// Performance monitoring
tasks.withType<JavaExec> {
    jvmArgs.addAll(listOf(
        "-XX:+UseG1GC",
        "-XX:MaxGCPauseMillis=200",
        "-Xms512m",
        "-Xmx2g"
    ))
}
