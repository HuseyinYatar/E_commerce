# 📑 Project Documentation: Saga & Outbox System

### 🧭 Navigation
* [1. Architectural Design](#architectural-design)
* [2. Saga Pattern Flows](#saga-pattern-flows)
* [3. Project Description](#description)
    * [3.1 System Requirements](#system-requirements)
    * [3.2 Business Logic](#business-logic)
    * [3.3 Implementation Details](#implementation-details)

---

<a name="architectural-design"></a>
## 1. Architectural Design
The system is built on a **Microservices Architecture** utilizing the **Orchestration-based Saga Pattern** coupled with the **Transactional Outbox Pattern**. A central **Coordinator Service** manages the lifecycle of distributed transactions, while the Outbox pattern ensures atomic consistency between local database states and Kafka event streams.



---

<a name="saga-pattern-flows"></a>
## 2. Saga Pattern Flows
To maintain eventual consistency, the system uses asynchronous events triggered by an **Outbox Poller** or **CDC (Change Data Capture)** mechanism.

* **Happy Path:** 1. The **CordinatorService** updates its local DB state and inserts a "Command" record into its `outbox` table in one transaction.
    2. An **Outbox Relay** picks up the record and publishes it to the `order-requests` Kafka topic.
    3. The **OrderService** processes the event, saves the order, and writes an `ORDER_CREATED` event to its own `outbox` table.

* **Compensating Transactions:** 1. If a downstream service fails, it writes a `FAILURE` event to its `outbox`.
    2. The **CordinatorService** identifies the failure and writes a `ROLLBACK` command to its own `outbox`.

---

<a name="description"></a>
## 3. Description
Detailed technical requirements and implementation logic for the reliable Ecommerce platform.

<a name="system-requirements"></a>
### 1. System Requirements
* **At-Least-Once Delivery:** The Outbox pattern guarantees that every event persisted in the database will eventually reach Kafka.
* **Structured Logging:** Every service must use `net.logstash.logback.encoder.LogstashEncoder` to map `traceId` and `transactionId`.

<a name="business-logic"></a>
### 2. Business Logic
* **Atomicity:** Business state changes and event publishing must be wrapped in a `@Transactional` block.
* **Idempotency:** The Coordinator must use a `processed_messages` table to ignore duplicate Kafka events.

<a name="implementation-details"></a>
### 3. Implementation Details
1. **Outbox Table Schema:** Each service contains an `outbox` table with fields: `id`, `aggregate_id`, `type`, `payload` (JSON), and `status`.
2. **Message Relay:** A background task polls the `outbox` table for `PENDING` records and publishes them to Kafka 4.0.1.
3. **Logstash Correlation:** Logs generated include the `aggregate_id`, linking the DB transaction to the Kafka message in Kibana.
