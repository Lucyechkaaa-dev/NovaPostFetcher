# NovaPostFetcher

Java client for the [Nova Poshta API 2.0](https://developers.novaposhta.ua) covering every documented method, with an optional MySQL synchronizer for the address directories and waybills.

Requires JDK 21+ and Maven 3.8+. Anything tied to your account (waybills) needs an API key (business cabinet → Settings → Security → Create key). MySQL 8+ or MariaDB is needed only for the synchronizer.

## Build

```bash
mvn clean package -DskipTests    # → target/NovaPostFetcher-1.0-SNAPSHOT.jar
```

## Usage

```java
// packages: com.novapost.controller, com.novapost.model, com.novapost.db, com.novapost.service
NovaPostController np = NovaPostController.create(System.getenv().getOrDefault("NOVAPOST_API_KEY", ""));

np.getTracking("20400048799000").forEach(s -> System.out.println(s.number() + " — " + s.status()));  // works without a key
BigDecimal price = np.calculatePrice(cityFromRef, cityToRef, 2.0, 1000);                             // kg, UAH
InternetDocumentResponse waybill = np.createWaybill(routing, CargoDetails.ofSimple("Clothes", 1.5, 500));  // routing: sender and recipient Refs
byte[] label = np.printMarkingZebra(waybill.intDocNumber());                                         // PDF
```

Every other documented method is behind `np.getClient()`: `.address()`, `.counterparty()`, `.internetDocument()`, `.courier()`, `.scanSheet()`, `.common()`, `.tracking()`, `.print()` and others.

The synchronizer **truncates** the `nova_post_*` tables before it reloads them:

```java
new NovaPostSyncService(np.getClient(), new DatabaseManager(new DatabaseConfig(jdbcUrl, user, password)))
        .syncAll(30);    // settlements, warehouses, streets and the waybills of the last 30 days
```

## Tests

```bash
mvn clean test
```

Live checks against the real API are opt-in: `NOVAPOST_LIVE_KEYLESS=1` (one tracking request, no key) or `NOVAPOST_API_KEY=...` (read-only calls). API 2.0 has no sandbox: a key's calls go to its real account.

## License

[MIT](LICENSE)
