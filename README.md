# Kafka Log System

This project demonstrates a full **event-driven logging pipeline** using Kafka, including:

- **Producer API**: Sends user activity logs to Kafka  
- **Consumer Worker**: Validates and saves logs or routes them to **DLQ**  
- **Dockerized Infrastructure**: Kafka, Zookeeper, PostgreSQL for local testing  
- **Integration Tests**: Ensures functionality and reliability

> Built with **Java 17**, **Spring Boot 3**, **Spring Kafka 3**, **PostgreSQL**, **Docker**, **JUnit5**, **Awaitility**, and **H2** for isolated testing.

---

## Features

- Produce and consume Kafka messages
- Save valid logs into PostgreSQL
- Route invalid messages to DLQ and persist them
- Integration tested with success, failure, concurrency, and bulk scenarios
- Dockerized infrastructure for easy local setup

## Kafka Topics

| Topic                  | Description                            |
|------------------------|----------------------------------------|
| `user-activity-log`     | Main topic for user activity logs      |
| `user-activity-log-dlq` | Dead Letter Queue for failed messages  |

## Repository Structure

```bash
kafka-log-system/
│
├── producer-api/                # Produces logs to Kafka via REST API
│   └── ...                      # Spring Boot application
│
├── consumer-worker/            # Consumes, validates, saves or DLQs
│   └── ...                      # Spring Boot + Kafka + PostgreSQL
│
├── infra/                      # Docker Compose infrastructure
│   └── docker-compose.yml
│
└── README.md                   
```

## Infrastructure (Docker Compose)

Docker Compose brings up the following services:

| Service         | Description                       | Port |
|-----------------|-----------------------------------|------|
| Zookeeper       | Kafka cluster coordinator         | 2181 |
| Kafka           | Kafka broker                      | 9092 |
| PostgreSQL      | Logs and DLQ storage               | 5432 |
| Producer API    | REST API for producing logs        | 8080 |
| Consumer Worker | Kafka consumer + DLQ handler       | 8081 |

Launch all services:

```bash
cd infra
docker-compose up -d
```

## Test Scenarios

1. Successful Message Consumption
- Given a valid user log
- When produced to user-activity-log
- Then it’s saved to user_log table

2. Failure → DLQ Routing
- Given an invalid message (e.g., missing fields)
- When consumed
-	Then it’s:
  - Rejected with validation errors
  - Routed to user-activity-log-dlq
  - Saved in dlq_message table with raw message and error info

3. Concurrent Processing
- Send multiple valid messages concurrently
- Verify all are processed and persisted without loss

4. Bulk Load Testing
- Send 100+ valid messages
- Ensure system stability under load

## Integration Testing

Located in consumer-worker

-	Uses real Kafka broker (not embedded)
- DB: H2 in-memory for isolation
- Frameworks: JUnit5 + Awaitility + AssertJ

```bash
./gradlew test
```

## Example Kafka Messages

✅ Valid Message

```json
{
  "userId": "jin",
  "action": "click",
  "timestamp": "2025-04-28T12:00:00"
}
```

❌ Invalid Message (goes to DLQ)

```json
{
  "userId": "jin"
}
```

## How to Run the System

1.	Start infrastructure

```bash
cd infra
docker-compose up -d
```

2.	Build producer & consumer

```bash
cd producer-api
./gradlew build

cd ../consumer-worker
./gradlew build
```

3.	Send requests

```bash
curl -X POST http://localhost:8080/api/logs \
     -H "Content-Type: application/json" \
     -d '{"userId":"jin", "action":"click", "timestamp":"2025-04-28T12:00:00"}'
```

## Future Improvements
- Add retry mechanisms before DLQ
- Implement schema validation (e.g., Avro or JSON Schema)
- Add monitoring (e.g., DLQ alerting)
- Provide UI or API to reprocess DLQ messages
