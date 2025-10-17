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

## Tech Stack
| Component    | Primary Technologies     |
| :----------- | ------------------------ |
| Backend      | Spring Boot, Spring Cloud|
| Messaging    | Apache Kafka             |
| Databases    | PostgreSQL               |
| Search       | Apache Lucene            |
| Storage      | MinIO                    |
| Execution    | NSJail                   |
