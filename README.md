<p align="center">
  <a href="#"><img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/></a>
  <a href="https://spring.io"><img src="https://img.shields.io/badge/Spring_Boot-6cb52d?style=for-the-badge&logo=Spring&logoColor=white" alt="Spring"/></a>
          <a href="[https://www.keycloak.org](https://spring.io/projects/spring-cloud)"><img src="https://img.shields.io/badge/Spring_Cloud-6cb52d?style=for-the-badge&logo=icloud&logoColor=white" alt="Spring Cloud"/></a>
    <a href="https://www.red-gate.com/products/flyway/"><img src="https://img.shields.io/badge/flyway-c60400?style=for-the-badge&logo=flyway&logoColor=white"  alt="Flyway"/></a>
  <a href="https://kafka.apache.org"><img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white" alt="Apache Kafka"/></a>
      <a href="https://kubernetes.io"><img src="https://img.shields.io/badge/kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white" alt="Kubernetes"/></a>
      <a href="https://www.keycloak.org"><img src="https://img.shields.io/badge/keycloak-4D4D4D?style=for-the-badge&logo=keycloak&logoColor=white" alt="Keycloak"/></a>
    <a href="https://www.postgresql.org"><img src="https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/></a>
  <a href="https://www.docker.com"><img src="https://img.shields.io/badge/docker-257bd6?style=for-the-badge&logo=docker&logoColor=white" alt="Docker, Docker Compose"/></a>
  <a href="https://git-scm.com"><img src="https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white" alt="Git"></a>
  <a href="#"><img src="https://img.shields.io/badge/JWT-333?style=for-the-badge&logo=JSON-Web-Tokens&logoColor=white" alt="JSON Web Tokens"/></a>
  <a href="https://spring.io"><img src="https://img.shields.io/badge/swagger-6cb52d?style=for-the-badge&logo=swagger&logoColor=white&titleColor=white" alt="Swagger"/></a>
   <a href="https://react.dev"><img src="https://img.shields.io/badge/React-%23353947?style=for-the-badge&logo=react" alt="React"></a>
    <a href="https://www.typescriptlang.org"><img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="Typescript"/></a>
  <a href="https://tailwindcss.com"><img src="https://img.shields.io/badge/TailwindCSS-37bdf8?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="Tailwind"/></a>
</p>
<img width="1403" height="1017" alt="Working example" src="https://github.com/user-attachments/assets/219a5727-46db-4e28-883f-13333d78db13" />

# Get Started
Follow these steps to run the project locally.

### Prerequisites
Make sure you have the following installed:

- Docker
- Git

### Clone the repository
```shell
git clone https://github.com/danyazero/LeetCode.git
cd LeetCode
```
### Build the project
```shell
make build
```

### Configure local domains
Add the following entries to your /etc/hosts file:
```diff
-127.0.0.1       localhost
+127.0.0.1       localhost swagger.localhost noty.localhost auth.localhost problem.localhost submission.localhost
```
### Run the project

```shell
docker compose up -d
```

### Access the services

- **Frontend (Application)** — http://localhost  
- **Keycloak (Auth setup & admin panel)** — http://auth.localhost  
- **Swagger (API documentation)** — http://swagger.localhost/swagger-ui.html  


# System Architecture
Built on an Event-Driven Architecture (EDA), this system implements loosely coupled microservices for maximum agility and independent scaling. All core operations are handled asynchronously using a Message Queue, which guarantees system resilience and enables real-time notifications for user feedback.


<img width="3912" height="2019" alt="LeetCode-System-Design" src="https://github.com/user-attachments/assets/b786e33b-6026-4691-8ad7-7e7897c9f7f7" />


> [!NOTE]
> Initially, Apache Lucene was considered for full-text search, but PostgreSQL’s built-in FTS was chosen instead due to simplicity, reliability, and reduced data duplication.

### Components
1. **API Gateway**
  - Acts as the single entry point for all client requests.
  - Handles HTTP API requests and WebSocket connections.
  - Provides centralized authentication and routing logic.
2. **Problem Service**
  - Manages problem statements, metadata, and associated test cases.
  - Stores problem data in Problem DB and files in S3.
  - Uses a Lucene index for full-text search across problems.
  - Supplies test cases to the `Executor Service` during code evaluation.
3. **Submission Service**
  - Accepts user submissions via the `API Gateway`.
  - Stores submission metadata in Submission DB.
  - Publishes SubmissionCreated events to the `Message Queue` for asynchronous processing.
4. **Executor Service**
  - Listens for new submissions from the `Message Queue`.
  - Executes the submitted code inside an isolated environment (nsjail).
  - Fetches problem test cases from the `Problem Service`.
  - Sends EvaluationResult events to the queue during & after code execution.
5. **Notification Service**
  - Maintains active WebSocket connections with clients through the `API Gateway`.
  - Subscribes to the `Message Queue` to receive Evaluation events.
  - Sends real-time notifications to the corresponding users about their submission results.
6. **User Service (Keycloak)**
  - Manages user accounts, registration, and authentication data.
  - Stores user profiles and credentials in related database.
  - Issues and refreshes authentication tokens.
  - Provides APIs for other services that require user information.
7. **Message Queue**
  - Serves as the central communication bus for all asynchronous events.
  - Ensures decoupled communication between services.
  - Allows horizontal scaling of processing services without tight coupling.

# Probem Service

### Overview
The Problem Service is responsible for managing all coding problem–related data in the system.
It acts as the central knowledge base for the platform, storing detailed information about coding problems, their metadata, categorization, and associated testcases used for automatic evaluation.

### Database
The schema is fully normalized (3NF), avoiding data duplication and ensuring referential integrity.
Tags, difficulties, and testcases are modular — each can evolve independently.

<img width="3068" height="1604" alt="LeetCode-Problem-Service-Database" src="https://github.com/user-attachments/assets/c9f06771-c9bd-406e-9d9f-c96eb396bfa5" />

| Table             | Description                                                                                                                                                                        |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`Problem`**     | Core table representing a coding challenge. Contains metadata such as title, description, and difficulty.                                                                          |
| **`Difficulty`**  | Defines available difficulty levels (`Easy`, `Medium`, `Hard`, etc.) and provides referential consistency for the `Problem` table.                                                 |
| **`Tag`**         | Contains reusable tags (topics like `arrays`, `dp`, `graph`) that classify problems for search and filtering.                                                                      |
| **`Problem_Tag`** | A many-to-many relation linking problems with multiple tags. Allows flexible topic categorization and efficient querying.                                                          |
| **`Testcase`**    | References test files stored in S3 (or another object storage). Each testcase belongs to a single problem and is used by the Executor Service during code evaluation. |

# Submission Service

The Submission Service is responsible for managing the entire lifecycle of a code submission — from receiving and validating user code, storing it in the database and S3 storage, to triggering execution and tracking evaluation results.
It publishes submission events to the message queue for asynchronous execution by the Executor Service, and updates the submission status upon receiving results from queue (EDA).

### Database
The schema is fully normalized (3NF), avoiding data duplication and ensuring referential integrity.

<img width="767" height="441" alt="LeetCode-Database-Problem-Service" src="https://github.com/user-attachments/assets/93d08750-4218-4a29-a4f1-191a278a1f36" />


| Table               | Description                                                                                                                                                                        |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`Language`**      | Contains information about all supported programming languages for submissions.                                                                                                    |
| **`Submission`**    | The main table storing all user submissions. Each entry references the user, the problem, and the language used.                                                                   |
| **`Events`**        | Tracks the lifecycle of each submission by recording state changes. This enables asynchronous monitoring and debugging of submission processing in message-driven environments.    |


