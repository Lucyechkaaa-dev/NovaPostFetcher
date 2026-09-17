# 📦 NovaPostFetcher

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0%2B-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> A modern, resilient Java client and database synchronizer for the **[Nova Post (Nova Poshta) API 2.0](https://developers.novaposhta.ua)** address directory.

---

## ⚡ Highlights

- 🚀 **Modern Java Stack**: Built on Java 21+ using immutable `record` DTOs, Jackson 2, and asynchronous `java.net.http.HttpClient`.
- 🛡️ **Zero Hardcoded Secrets**: Strict CLI argument parsing with zero credentials, tokens, or personal info stored in the codebase.
- 🔁 **Resilient Rate-Limiter**: Automatic exponential backoff handling both HTTP `429` and payload-level `[Too many requests]` responses.
- 🗄️ **High-Performance DB Sync**: Auto-provisions schema (`nova_post_settlements`, `nova_post_warehouses`), truncates stale state, and batch-inserts fresh data with `ON DUPLICATE KEY UPDATE` and JDBC batch rewriting.

---

## 🏗️ Architecture & Data Flow

```text
  [ Nova Post API 2.0 ]
           │
           │  HTTP POST (paginated, with rate-limiting & backoff)
           ▼
    NovaPostClient
           │
           ▼
   NovaPostSyncService
           │
           │  JDBC Batch PreparedStatement (rewriteBatchedStatements=true)
           ▼
 [ MySQL / MariaDB Database ]
   ├── nova_post_settlements   (Cities, towns, villages & administrative regions)
   └── nova_post_warehouses    (Branches, lockers, working hours & weight limits)
```

---

## 📦 Supported API Methods

| Model | Called Method | Description |
|---|---|---|
| `AddressGeneral` | `getSettlements` | Full directory of Ukrainian settlements (cities, towns, villages) |
| `AddressGeneral` | `getWarehouses` | Full directory of Nova Post branches and parcel lockers (поштомати) |
| `Address` | `searchSettlements` | Fast interactive lookup for settlement auto-complete |
| `Address` | `searchSettlementStreets` | Street search within a settlement for direct door delivery |

---

## 🚀 Quick Start

### 1. Prerequisites

- **JDK 21** or higher
- **Maven 3.8+**
- **MySQL / MariaDB** (local or remote)
- A Nova Post API key (obtained from your Nova Post Business Account)

### 2. Build

```bash
git clone https://github.com/your-username/NovaPostFetcher.git
cd NovaPostFetcher
mvn clean package -DskipTests
```

The executable JAR will be located at `target/NovaPostFetcher-1.0-SNAPSHOT.jar`.

---

## 💻 CLI Usage

```bash
java -jar target/NovaPostFetcher-1.0-SNAPSHOT.jar \
  --api-key=<YOUR_NOVA_POST_API_KEY> \
  --db-url="jdbc:mysql://localhost:3306/novapost?rewriteBatchedStatements=true&useSSL=false&serverTimezone=UTC" \
  --db-user=root \
  --db-password=your_password
```

### Command-Line Arguments

| Option | Short Flag | Required | Default | Description |
|---|:---:|:---:|:---:|---|
| `--api-key=<KEY>` | `-k` | **Yes** | — | Your Nova Post API Key |
| `--db-url=<URL>` | | **Yes** | — | JDBC connection URL |
| `--db-user=<USER>` | `-u` | **Yes** | — | Database username |
| `--db-password=<PWD>` | `-p` | **Yes** | — | Database password |
| `--delay=<MS>` | `-d` | No | `750` | Sleep delay between pagination requests in ms |
| `--api-url=<URL>` | | No | `https://api.novaposhta.ua/v2.0/json/` | Nova Post API entrypoint |
| `--help` | `-h` | No | — | Display usage guidelines and exit |

---

## 🧪 Testing

Run test suite (includes JSON serialization tests, CLI argument parser tests, and conditional integration tests):

```bash
mvn clean test
```

To run the integration test against your local database:

```bash
mvn test -Dtest=DatabaseManagerIntegrationTest \
  -Dtest.db.url="jdbc:mysql://localhost:3306/test?rewriteBatchedStatements=true&useSSL=false&serverTimezone=UTC" \
  -Dtest.db.user=root \
  -Dtest.db.password=your_password
```

---

## 📝 License

Distributed under the [MIT License](LICENSE).
