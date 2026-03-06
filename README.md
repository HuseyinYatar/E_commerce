# 📑 Project Documentation: Saga & Outbox System

### 🧭 Navigation
* [1. Architectural Design](#architectural-design)
* [2. Saga Pattern Flows](#saga-pattern-flows)
    * [2.1 Happy Path Sequence](#happy-path-sequence)
    * [2.2 Compensating Transaction: Inventory Failure](#inventory-failure)
    * [2.3 Compensating Transaction: Payment Failure](#payment-failure)
* [3. Project Description](#description)

---

<a name="architectural-design"></a>
<details>
<summary><h2>1. Architectural Design (Click to expand)</h2>
<img src="saga-flows-images/architectural_design.png" width=800 height=600>
</summary>

The system is built on a **Microservices Architecture** utilizing the **Orchestration-based Saga Pattern** coupled with the **Transactional Outbox Pattern**. A central **Coordinator Service** manages the lifecycle of distributed transactions across the Order, Inventory, and Payment services.

* **Reliability Layer:** **Transactional Outbox Table** ensures that database updates and message publishing are atomic.
* **Messaging Engine:** Apache Kafka 4.0.1 (KRaft mode).
* **Observability:** Structured JSON logging via `LogstashEncoder` for centralized ELK monitoring.

</details>

---

<a name="saga-pattern-flows"></a>
<details>
<summary><h2>2. Saga Pattern Flows (Click to expand)</h2></summary>

The Saga Orchestrator coordinates distributed transactions. If any step fails, the Coordinator triggers **Compensating Transactions** to revert the system to a consistent state.


---

<a name="happy-path-sequence"></a>
<details>
<summary><h3>2.1 Happy Path Sequence (Click to expand)</h3>
<img src="saga-flows-images/happy_path.png" width=800 height=600>
</summary>


The standard successful transaction flow consists of 8 steps:
1.  **Order Place Request:** Client sends request to **Order Service**.
2.  **Order Created:** **Order Service** notifies **Coordinator**.
3.  **Check Inventory:** **Coordinator** requests stock from **Inventory Service**.
4.  **Checked Inventory:** **Inventory Service** confirms availability.
5.  **Start Payment:** **Coordinator** triggers **Payment Service**.
6.  **Finish Payment:** **Payment Service** confirms successful transaction.
7.  **Order Placed (Internal):** **Coordinator** finalizes state with **Order Service**.
8.  **Order Placed (Client):** **Order Service** confirms success to the **Client**.

</details>

---

<a name="inventory-failure"></a>
<details>
<summary><h3>2.2 Compensating Transaction: Inventory Failure (Click to expand)</h3>
<img src="saga-flows-images/inventory_crashed.png" width=800 height=600>
</summary>


When the inventory check fails, the system performs an immediate rollback:
1.  **Steps 1-3:** Proceed as normal (Order Place $\rightarrow$ Order Created $\rightarrow$ Check Inventory).
2.  **Inventory Check Failed (Step 4):** **Inventory Service** notifies **Coordinator** of insufficient stock.
3.  **Order Canceled (Step 9):** **Coordinator** sends a cancellation command to **Order Service**.
4.  **Order Canceled (Step 10):** **Order Service** notifies the **Client** that the order could not be completed.

</details>

---

<a name="payment-failure"></a>
<details>
<summary><h3>2.3 Compensating Transaction: Payment Failure (Click to expand)</h3>
<img src="saga-flows-images/payment_crashed.png" width=800 height=600>
   </summary>


If payment fails after inventory is reserved, a full reversal is required:
1.  **Steps 1-5:** Proceed through Order Creation and Inventory Reservation.
2.  **Payment Failed (Step 6):** **Payment Service** notifies **Coordinator** of a transaction failure.
3.  **Inventory-Reverse (Step 8):** **Coordinator** commands **Inventory Service** to release the reserved stock.
4.  **Order Canceled (Step 9):** **Coordinator** commands **Order Service** to update status to Canceled.
5.  **Order Canceled (Step 10):** **Order Service** provides final failure notification to the **Client**.

</details>
</details>

---

<a name="description"></a>
<details>
<summary><h2>3. Project Summary (Click to expand)</h2></summary>

###  Core Objective
The primary goal of this project is to implement a **resilient, distributed E-commerce ecosystem** capable of handling complex transactions across multiple microservices (Order, Inventory, and Payment). The system ensures that even in the event of network flickers, service crashes, or business logic failures, the data remains consistent without using high-latency global locks.

###  Reliability via Transactional Outbox
To solve the "Dual Write" problem, we implemented the **Transactional Outbox Pattern**. 
* **The Problem:** Typically, a service updates its database and *then* sends a Kafka message. If the service crashes after the DB update but before sending the message, the system becomes inconsistent.
* **The Solution:** We write the business state and the outgoing Kafka event into the same local database in a single atomic transaction. A background **Relay Task** then ensures the event reaches Kafka 4.0.1, guaranteeing **At-Least-Once Delivery**.



###  Coordination via Orchestrated Saga
The **Cordinator Service** acts as the central brain. It does not contain business logic itself but manages the "State Machine" of an order:
* **Decoupling:** Services don't need to know about each other; they only talk to the Coordinator.
* **Failure Handling:** If a downstream service fails (like a Payment rejection), the Coordinator knows exactly which "Compensating Transactions" to trigger (e.g., releasing reserved inventory) to roll back the system state.



###  Monitoring & Traceability
By integrating the **Logstash-Logback-Encoder**, every event in the Saga and every record in the Outbox is tagged with a unique `correlationId`. This allows us to visualize the entire life of an order—from the initial click to the final rollback—within a single view in the **ELK Stack (Kibana)**.
</details>

</details>

</details>
