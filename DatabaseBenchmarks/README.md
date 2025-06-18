# TigerBeetle Virtual Threads Benchmark

This experiment measures how long it takes to execute **10,000** individual transfers between random accounts using TigerBeetle and Java virtual threads.

## Overview
- **Language:** Java 21+ with Project Loom virtual threads
- **Library:** TigerBeetle Java client v0.16.x
- **Testbed:** Local TigerBeetle cluster (no replication), Docker or native install on laptop
- **Machine:** MacBook Pro i9, 32 GB RAM

## What the code does
1. **Establishes a connection** to a single-node TigerBeetle cluster on port 3000.
2. **Spawns a pool** of 10000 virtual threads.
3. **Submits 10,000 tasks**, each task does:
    - Generate random IDs for debit account, credit account and transfer.
    - Create both accounts if they don’t exist.
    - Issue a transfer of `amount = 10`.
    - Lookup both account balances.
    - Verify debit and credit were applied correctly.
4. **Shuts down** the thread pool and waits for up to 10 s for all tasks to finish.
5. **Prints elapsed time** for the entire operation.

## Results
- **Total transfers:** 10,000
- **Elapsed time:** 9 000 ms (≈ 9 s)
- **Replication factor:** 1 (no replication)

## Code

You can find the code [here](src/main/java/reactive/TigerBeetleFeature.java)

## How to run

```bash
# 1. Download TigerBeetle:

curl -Lo tigerbeetle.zip https://mac.tigerbeetle.com && unzip tigerbeetle.zip ./tigerbeetle version

# 2. Set the config of the database like cluster, replica
./tigerbeetle format --cluster=0 --replica=0 --replica-count=1 --development ./0_0.tigerbeetle

# 3. Run TigerBeetle

./tigerbeetle start --addresses=3000 --development ./0_0.tigerbeetle

# 3. Execute the benchmark JAR:
java -jar target/DatabaseBenchmarks-1.0.0.jar


```

