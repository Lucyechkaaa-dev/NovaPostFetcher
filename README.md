# NovaPostFetcher

A Java client and synchronization service for the [Nova Post (Nova Poshta) API 2.0](https://developers.novaposhta.ua).

## Features

- **Models supported**: `AddressGeneral` / `Address` lookup methods:
  - Settlements directory (`getSettlements`)
  - Warehouses / Branch & locker directory (`getWarehouses`)
  - Interactive settlement search (`searchSettlements`)
  - Street search (`searchSettlementStreets`)
- **Resilience**: Automatic retry with exponential backoff for HTTP 429 and rate-limiting payloads (`[Too many requests]`).
- **Database Synchronization**:
  - Automatically provisions tables (`nova_post_settlements`, `nova_post_warehouses`) with necessary indexes.
  - Clears/truncates previous records and batch-inserts fresh data with duplicate key handling.
- **Zero Hardcoded Secrets**: All credentials and connection URLs are supplied strictly via command-line arguments.

## Requirements

- Java 21+
- Maven 3.8+
- MySQL / MariaDB

## CLI Usage

```bash
java -jar target/NovaPostFetcher-1.0-SNAPSHOT.jar \
  --api-key=<YOUR_NOVA_POST_API_KEY> \
  --db-url=jdbc:mysql://localhost:3306/your_db \
  --db-user=db_user \
  --db-password=db_password
```

### Options

| Option | Short Flag | Required | Description |
|---|---|---|---|
| `--api-key=<KEY>` | `-k <KEY>` | Yes | Nova Post API key |
| `--db-url=<URL>` | | Yes | JDBC URL (e.g. `jdbc:mysql://localhost:3306/mydb`) |
| `--db-user=<USER>` | `-u <USER>` | Yes | Database username |
| `--db-password=<PWD>` | `-p <PWD>` | Yes | Database password |
| `--delay=<MS>` | `-d <MS>` | No | Delay between paginated API requests in ms (default: `750`) |
| `--api-url=<URL>` | | No | Nova Post API endpoint (default: `https://api.novaposhta.ua/v2.0/json/`) |
| `--help` | `-h` | No | Display usage information |

## Build & Test

```bash
# Compile and run unit tests
mvn clean test

# Package JAR
mvn package
```
