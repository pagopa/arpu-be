# arpu-be

This application represent the BFF of **Area Riservata Piattaforma Unitaria** product.

See [p4pa-doc](https://github.com/pagopa/p4pa-doc) for further documentation.

## 🧱 Role

* To expose data towards ARpu FE.

## 🌐 APIs
See [OpenAPI](openapi/generated.openapi.json), exposed through the following path:
* `/swagger-ui/index.html`

See [Postman collection](/postman/pagopa-arpu-be-E2E.postman_collection.json) and [Postman Environment](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1063813158/ARp+-+Environment+collection+postman).

### 📌 Common HTTP status returned:
* `200`: Successful operation;
* `401`: Invalid access token provided, thus a new login is required;
* `403`: Trying to access a not authorized resource.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
    * Liveness: `/actuator/health/liveness`
    * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
    * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* Redis

### 🧩 Microservices
* [p4pa-auth](https://github.com/pagopa/p4pa-auth):
    * To build access token towards p4pa-citizen;
* [p4pa-citizen](https://github.com/pagopa/p4pa-citizen):
    * To access to domain data and operations.

### 🌍 External
* External OAuth2 Authorization Server;
* ZenDesk.
* [Google reCaptcha openAPI](openapi/external/google-recaptcha.openapi.yaml): To handle Google reCaptcha integration;

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV               | DESCRIPTION                                                                     | DEFAULT               |
|-------------------|---------------------------------------------------------------------------------|-----------------------|
| SERVER_PORT       | Application server listening port                                               | 8080                  |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                            | DEFAULT |
|---------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                             | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                           | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                      | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                           | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                        | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://raw.githubusercontent.com/pagopa/p4pa-doc/refs/heads/main/reference/technical-docs/Logging.pdf)              | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://raw.githubusercontent.com/pagopa/p4pa-doc/refs/heads/main/reference/technical-docs/Logging.pdf)         | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://raw.githubusercontent.com/pagopa/p4pa-doc/refs/heads/main/reference/technical-docs/Logging.pdf) | INFO    |

#### 🔁 Integrations

##### 🗄️ Resources
| ENV            | DESCRIPTION       | DEFAULT   |
|----------------|-------------------|-----------|
| REDIS_HOST     | Redis server host | localhost |
| REDIS_PORT     | Redis server port | 6380      |
| REDIS_PASSWORD | Redis password    |           |

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                | DESCRIPTION                                    | DEFAULT |
|------------------------------------|------------------------------------------------|---------|
| P4PA_AUTH_BASE_URL                 | p4pa-auth microservice URL                     |         |
| P4PA_AUTH_MAX_ATTEMPTS             | p4pa-auth API max attempts                     | 3       |
| P4PA_AUTH_WAIT_TIME_MILLIS         | p4pa-auth retry waiting time (milliseconds)    | 500     |
| P4PA_AUTH_PRINT_BODY_WHEN_ERROR    | To print body when an error occurs             | true    |
| P4PA_CITIZEN_BASE_URL              | p4pa-citizen microservice URL                  |         |
| P4PA_CITIZEN_MAX_ATTEMPTS          | p4pa-citizen API max attempts                  | 3       |
| P4PA_CITIZEN_WAIT_TIME_MILLIS      | p4pa-citizen retry waiting time (milliseconds) | 500     |
| P4PA_CITIZEN_PRINT_BODY_WHEN_ERROR | To print body when an error occurs             | true    |

##### 🌍 External services
| ENV                                      | DESCRIPTION                                                   | DEFAULT |
|------------------------------------------|---------------------------------------------------------------|---------|
| AUTH_CLIENT_AUTHORIZATION_URI            | External OAuth Authorization server URL                       |         |
| AUTH_CLIENT_TOKEN_URI                    | External OAuth Authorization server token URL                 |         |
| AUTH_CLIENT_JWK_URI                      | External OAuth Authorization server JWKS URL                  |         |
| AUTH_ISSUER_URI                          | External OAuth Authorization server issuer                    |         |
| AUTH_CLIENT_ID                           | External OAuth Authorization's registered client clientId     |         |
| AUTH_CLIENT_SECRET                       | External OAuth Authorization's registered client clientSecret |         |
| AUTH_CLIENT_REDIRECT_URI                 | External OAuth Authorization's registered client redirect URL |         |
| HELP_CENTER_URL                          | External Help Center URL                                      |         |
| ZENDESK_ACTION_URL                       | External ZenDesk URL                                          |         |
| ASSISTANCE_ZENDESK_PRODUCT_ID            | External ZenDesk product id                                   |         |
| ASSISTANCE_ZENDESK_ORGANIZATION          | External ZenDesk organization                                 |         |
| JWT_TOKEN_ASSISTANCE_ZENDESK_PRIVATE_KEY | External ZenDesk private key                                  |         |
| GOOGLE_RECAPTCHA_SERVER_BASE_URL         | Google reCaptcha service URL                                  |         |
| GOOGLE_RECAPTCHA_MAX_ATTEMPTS            | Google reCaptcha API max attempts                             | 3       |
| GOOGLE_RECAPTCHA_WAIT_TIME_MILLIS        | Google reCaptcha retry waiting time (milliseconds)            | 500     |
| GOOGLE_RECAPTCHA_PRINT_BODY_WHEN_ERROR   | To print body when an error occurs                            | true    |

#### 💼 Business logic
| ENV                               | DESCRIPTION                                  | DEFAULT |
|-----------------------------------|----------------------------------------------|---------|
| WHITE_LIST_USERS                  | CF list of enabled users                     |         |
| GOOGLE_RECAPTCHA_PROTECTED_PATHS  | List of APIs protected with Google reCaptcha |         |

#### 🔑 keys
| ENV                              | DESCRIPTION                                                                                                                                                                                                                                                            | DEFAULT              |
|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|
| JWT_TOKEN_AUDIENCE               | The aud claim set on the generated access token                                                                                                                                                                                                                        | application-audience |
| JWT_TOKEN_EXPIRATION_SECONDS     | Access token expiration (seconds)                                                                                                                                                                                                                                      | 3600                 |
| JWT_TOKEN_PRIVATE_KEY            | JWT private key                                                                                                                                                                                                                                                        |                      |
| JWT_TOKEN_PUBLIC_KEY             | JWT public key                                                                                                                                                                                                                                                         |                      |
| ACCESS_ORGANIZATION_MODE_ENABLED | If true, it will expect the presence of the access organization inside the ID Token. Thus, it will register te relation between the operator and the relation with the provided roles. If disabled, the admin should register the associations using the provided API. | true                 |
| GOOGLE_RECAPTCHA_SECRET          | Google reCaptcha secret                                                                                                                                                                                                                                                |                      |
| GOOGLE_RECAPTCHA_ENABLED         | If true, the paths configured in GOOGLE_RECAPTCHA_PROTECTED_PATHS will expect the header "X-recaptcha-token" to be populated with the Google reCaptcha token                                                                                                       | false                |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```

### ⚖️ Generate dependencies licenses
```sh
./gradlew generateLicenseReport
```
