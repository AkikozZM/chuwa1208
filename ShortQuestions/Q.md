Question: When should you use multi-threading versus async/non-blocking I/O? Give specific examples of scenarios for each approach.

Answer: **Multi-threading** is best when you have CPU-bound work that benefits from parallel execution on multiple cores, or when you must use blocking APIs/libraries that cannot be made async. Use it when: (1) doing heavy computation (image processing, video encoding, scientific simulation); (2) using libraries or APIs that block (e.g., legacy JDBC, file I/O without NIO); (3) you need true parallelism and have multiple cores.

**Async/non-blocking I/O** is best when work is I/O-bound and you want high concurrency with few threads. Use it when: (1) building high-throughput servers (HTTP, WebSocket) handling many concurrent connections; (2) calling many external services or databases where most time is waiting on network; (3) you want to avoid thread-per-request and reduce context-switching and memory (each thread has a stack).

Examples: Use multi-threading for a batch job that runs cryptographic hashes on many files (CPU-bound). Use async I/O for a REST gateway that proxies requests to dozens of microservices (I/O-bound, many concurrent calls).

Question: What are Virtual Threads in Java 21? Explain how they differ from platform threads and when you should (or should not) use them.

Answer: **Virtual threads** (project Loom) are lightweight threads managed by the JVM; many virtual threads are scheduled onto a small pool of platform (OS) threads. They have very small stack footprint and cheap creation, so you can have millions of them.

**Differences from platform threads:** (1) One platform thread = one OS thread; virtual threads are JVM-managed and multiplexed. (2) Blocking a virtual thread (e.g., I/O) does not block the underlying platform thread—the JVM can run other virtual threads on it. (3) Virtual threads are cheap to create; platform threads are expensive (stack size ~1MB, limited total count).

**Use virtual threads when:** You have many concurrent, mostly blocking tasks (e.g., "thread per request" servers, I/O-bound workloads). They allow a familiar blocking style with high scalability.

**Avoid or be careful when:** (1) Doing CPU-bound work with few, long-running tasks—platform threads or a small pool may be simpler. (2) Using synchronized blocks or native code that pins the virtual thread to a platform thread (reduces benefit). (3) Relying on thread-local semantics at very large scale (prefer scoped values or other patterns).

Question: Compare Lock-based synchronization and CAS (Compare-And-Swap). When would you choose one over the other?

Answer: **Lock-based (e.g., synchronized, ReentrantLock):** A thread acquires exclusive access; others block until the lock is released. Good for complex critical sections, multiple steps, or when you need conditions/wait-notify.

**CAS (Compare-And-Swap):** Atomic hardware instruction that updates a value only if it matches an expected value; no blocking, but threads may spin retrying. Used in lock-free data structures (e.g., AtomicInteger, ConcurrentHashMap internals).

**Choose locks when:** You have multi-step updates that cannot be expressed as a single atomic compare-and-swap; you need fairness, reentrancy, or condition variables; or contention is high and spinning would waste CPU.

**Choose CAS when:** Updates are simple (e.g., counter increment, single pointer swap), contention is low to moderate, and you want to avoid blocking and deadlock risk. CAS can scale better under low contention but may cause contention under high contention (compare with LongAdder for counters).

Question: What are the four necessary conditions for a deadlock to occur? Explain how "lock ordering" prevents deadlock.

Answer: The four conditions (all must hold) are:

1. **Mutual exclusion** — resources cannot be shared (e.g., only one thread can hold a lock).
2. **Hold and wait** — a thread holds at least one resource while waiting for another.
3. **No preemption** — resources cannot be forcibly taken from a thread.
4. **Circular wait** — there exists a cycle of threads each waiting for a resource held by the next (e.g., T1 holds A, waits for B; T2 holds B, waits for A).

**Lock ordering:** Assign a global order to all lockable resources (e.g., by address or by a fixed ordering). Every thread must acquire locks only in that order (e.g., always A before B). This removes the possibility of circular wait: if T1 holds A and wants B, and T2 holds B and wants A, one of them would have had to acquire the "smaller" lock first, so they cannot form a cycle. Thus deadlock is prevented.

Question: What is the formula for calculating optimal thread pool size?

Answer: For **CPU-bound** work:  
 **threads ≈ number of CPU cores** (or cores + 1 to keep CPUs busy during occasional blocking). More threads add context switching with little benefit.

For **I/O-bound** work (threads block on I/O):  
 **threads ≈ number of cores × (1 + wait time / compute time)**  
 So if a task spends 90% of time waiting (e.g., on network), you can use many more threads than cores to keep CPUs utilized. In practice this is often approximated as a multiple of cores (e.g., 2×–4×) or tuned based on throughput and latency goals.

Question: How would you size a thread pool for?

Answer: - **CPU-bound tasks (e.g., image processing, parsing):** Size ≈ number of CPU cores. Use a fixed pool (e.g., Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())).

- **I/O-bound tasks (e.g., HTTP calls, DB queries):** Size larger than cores, based on ratio of wait time to compute time: threads ≈ cores × (1 + W/C). Tune with load tests; avoid unbounded growth (use a bounded pool or queue).

- **Mixed workload:** Use separate pools (e.g., one small pool for CPU-bound, one larger for I/O-bound) or a single pool sized for the dominant factor and monitored for utilization and latency.

Question: What is exponential backoff with jitter? Why is jitter important when implementing retry logic?

Answer: **Exponential backoff:** After each failure, wait before retrying, with the delay increasing exponentially (e.g., 1s, 2s, 4s, 8s…) up to a cap. This gives failing services time to recover and reduces load on them.

**Jitter:** Randomization added to the backoff delay (e.g., delay = base × 2^attempt ± random factor, or multiply by a random value in [0.5, 1.5]). This spreads out retries across many clients.

**Why jitter matters:** Without jitter, many clients retry at the same time (e.g., all at 1s, then 2s), causing synchronized retry storms and prolonged overload or thundering herd. Jitter desynchronizes retries, smooths load, and improves the chance of recovery and success.

Question: Explain the three states of a Circuit Breaker (CLOSED, OPEN, HALF-OPEN) and how transitions between them work.

Answer: - **CLOSED:** Normal operation. Requests go through; failures are counted. If failures exceed a threshold (e.g., count or rate) within a window, transition to OPEN.

- **OPEN:** The circuit "trips"; requests fail immediately (or return a fallback) without calling the downstream service. After a timeout (e.g., 30s), transition to HALF-OPEN to test recovery.

- **HALF-OPEN:** A limited number of requests (e.g., one or a few) are allowed through. If they succeed, the circuit transitions to CLOSED (service is healthy). If they fail, transition back to OPEN and wait again before retrying.

This protects the downstream service from overload and gives it time to recover while failing fast for callers.

Question: Why is "exactly-once delivery" considered impossible in distributed systems? How can you achieve "exactly-once processing" instead?

Answer: **Why exactly-once delivery is considered impossible:** Networks and processes can fail at any time. You cannot distinguish "message not yet delivered" from "message lost" or "ack lost." To guarantee delivery, the sender must retry; the receiver may then get the same message multiple times. Without perfect, fault-tolerant consensus and no failures, you cannot guarantee exactly one delivery across the network.

**Exactly-once processing:** Accept that messages may be delivered more than once (at-least-once delivery), and make processing **idempotent**: design operations so that applying them multiple times with the same key/ID has the same effect as once (e.g., upsert by primary key, idempotency keys, deduplication in the consumer). Combined with at-least-once delivery and durable, transactional or log-based processing, you get exactly-once semantics from the application's perspective.

Question: Compare the three caching patterns: Cache-Aside, Write-Through, and Write-Behind. What are the trade-offs of each?

Answer: - **Cache-Aside (Lazy Loading):** App owns cache and DB. On read miss, app loads from DB and populates cache. Writes go to DB; cache is updated or invalidated by the app.  
 Trade-offs: Simple, cache only holds what is read. Risk of stale data if invalidation is wrong; read miss adds latency.

- **Write-Through:** App writes to cache and cache is responsible for writing to DB (synchronously). Reads go to cache; cache and DB stay in sync.  
  Trade-offs: Strong consistency, read miss only on cold start. Write latency includes DB write; cache must support write-through.

- **Write-Behind (Write-Back):** App writes to cache; cache asynchronously batches/flushes to DB. Reads served from cache.  
  Trade-offs: Low write latency and high write throughput. Risk of data loss if cache fails before flush; eventual consistency and more complex recovery.

Choose Cache-Aside for simplicity and flexibility; Write-Through when consistency is critical; Write-Behind when write performance matters and some delay/risk is acceptable.

Question: What is cache stampede (also known as cache breakdown)? Describe two solutions to prevent it.

Answer: **Cache stampede (cache breakdown):** When a popular cache entry expires or is evicted, many requests simultaneously see a miss and all trigger the same expensive computation or DB query to repopulate the cache. This causes a spike in load (and often latency or failures) on the backend.

**Two solutions:**

1. **Locking / coalescing (e.g., "single flight"):** Only one thread/process recomputes the value; others wait (or block) and then read the refreshed entry. Implement with a lock per key or a "promise" that many callers wait on. Reduces duplicate work and backend load.

2. **Probabilistic early expiration (e.g., "stochastic early revalidation"):** Before the entry expires, randomly allow one request to trigger a background refresh (e.g., if TTL is 90% done and random < 0.01, refresh in background). Other requests keep getting the slightly stale value until refresh completes. Spreads refresh over time and reduces thundering herd when TTL actually expires.
