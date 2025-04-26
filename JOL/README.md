# HashMap vs Chronicle Map — Memory Footprint Shoot‑out

---

| Map implementation | Total size | Logical data¹ | Structural overhead | Avg. overhead / entry |
|--------------------|-----------:|--------------:|--------------------:|----------------------:|
| **Java `HashMap` (on‑heap)** | **136 388 672 B** (≈ 130 MiB) | 96 000 000 B | 40 388 672 B | 40 B |
| **OpenHFT Chronicle Map (off‑heap)** | **29 396 992 B** (≈ 28 MiB) | 13 777 812 B | 15 619 180 B | 15 B |

> **Take‑away:** Chronicle Map slashes per‑entry overhead from **40 B → 15 B** and keeps overall memory ~4.6× smaller for one million `String → String` pairs.

¹ *“Logical data”* = UTF‑8 bytes of every key + value. `k0…k999 999`, `v0…v999 999`.

---

## 1. Why we ran this test

We wanted a **clean, apples‑to‑apples comparison** of the memory cost of a classic `java.util.HashMap` versus an off‑heap **Chronicle Map** when both hold the same one‑million simple key/value pairs.

* HashMap keeps every entry as a separate Java object → object headers, padding, GC pressure.
* Chronicle serialises the data contiguously into a direct‑memory slab and stores only tiny metadata per entry.

---

## 2. Environment

| Tool | Version |
|------|---------|
| **JDK** | 22 (Temurin 22.0.0+36) |
| **Maven** | 3.9.x |
| **OS** | Linux x86‑64 |
| **Chronicle Map** | 3.27‑ea (snapshot) |
| **JOL** | 0.17 |

Run‑time flags for Chronicle on JDK 22:

```bash
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
```

---

## 3. Experiment #1 — Vanilla `HashMap`

* **Code:** [`HashMapNodeSize.java`](./HashMapNodeSize.java)
* Fill the map with `N = 1 000 000` pairs.
* Use **JOL** to walk the full object graph and report:
    * *Total footprint* — every object reachable from the map.
    * *Logical data* — size of all keys & values minus two helper arrays.
    * Compute structural overhead = total − logical.

```text
Total HashMap...............: 136,388,672 bytes
Data (keys + values)........:  96,000,000 bytes
Structural overhead.........:  40,388,672 bytes
Average overhead per entry..:          40 bytes
```

### What drives the overhead?

* 32 B for each internal `HashMap.Node` (header + 3 references + padding).
* ~12–16 B per `String` object header + padding.
* 12 B array header per `byte[]` inside every `String`.

---

## 4. Experiment #2 — Chronicle Map (off‑heap)

* **Code:** [`ChronicleMapMemoryBreakdown.java`](./ChronicleMapMemoryBreakdown.java)
* Same dataset, off‑heap storage.
* Chronicle reports *off‑heap bytes* directly; logical UTF‑8 length computed manually.

```text
Total off-heap...............: 29,396,992 bytes  (28.04 MiB)
Logical data (UTF-8).........: 13,777,812 bytes  (13.14 MiB)
Structural overhead..........: 15,619,180 bytes  (14.90 MiB)
Average overhead per entry...: 15 bytes
```

### Why so much smaller?

* No per‑entry object headers — one packed structure per entry.
* Keys & values stored as raw UTF‑8 bytes in the same slab.
* JVM GC sees only **one** Java object (`ChronicleMap`), so no per‑object mark words.

---

