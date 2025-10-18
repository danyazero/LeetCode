## Tech Stack
| Component    | Primary Technologies     |
| :----------- | ------------------------ |
| Backend      | Spring Boot, Spring Cloud|
| Messaging    | Apache Kafka             |
| Databases    | PostgreSQL               |
| Search       | Apache Lucene            |
| Storage      | MinIO                    |
| Execution    | NSJail                   |

## System Architecture
Built on an Event-Driven Architecture (EDA), this system implements loosely coupled microservices for maximum agility and independent scaling. All core operations are handled asynchronously using a Message Queue, which guarantees system resilience and enables real-time notifications for user feedback.

<img width="3912" height="2019" alt="LeetCode-System-Design-Schema" src="https://github.com/user-attachments/assets/67b597a0-0266-433b-b073-872859819b23" />

## Components
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
6. **Message Queue**
  - Serves as the central communication bus for all asynchronous events.
  - Ensures decoupled communication between services.
  - Allows horizontal scaling of processing services without tight coupling.

## Probem Service

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

