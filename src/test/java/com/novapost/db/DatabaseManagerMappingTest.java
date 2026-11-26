package com.novapost.db;

import java.math.BigDecimal;

import com.novapost.model.InternetDocumentListItem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * No database needed: a fake JDBC driver records the DDL and answers metadata questions, so the test can check that
 * {@code mapInternetDocument} only reads columns the schema really creates (it once asked for {@code costOnSite}, a column
 * that never existed) and that old tables are migrated.
 */
class DatabaseManagerMappingTest{

	private static final String URL = "jdbc:novapost-fake:test";
	private static final List<String> EXECUTED = new ArrayList<>();
	private static final Set<String> EXISTING_COLUMNS = new HashSet<>();
	private static Driver driver;

	/** A real class (not a proxy): DriverManager only accepts drivers whose class it can load by name. */
	public static final class FakeDriver implements Driver{

		@Override
		public Connection connect(String url, Properties info){
			return acceptsURL(url) ? proxy(Connection.class, (m, a) -> switch(m){
				case "createStatement" -> statement();
				case "getMetaData" -> metaData();
				case "prepareStatement" -> preparedStatement((String) a[0]);
				case "close", "setAutoCommit", "commit", "rollback" -> null;
				default -> throw new UnsupportedOperationException("Connection." + m);
			}) : null;
		}

		@Override public boolean acceptsURL(String url){ return url != null && url.startsWith("jdbc:novapost-fake:"); }
		@Override public DriverPropertyInfo[] getPropertyInfo(String url, Properties info){ return new DriverPropertyInfo[0]; }
		@Override public int getMajorVersion(){ return 1; }
		@Override public int getMinorVersion(){ return 0; }
		@Override public boolean jdbcCompliant(){ return false; }
		@Override public java.util.logging.Logger getParentLogger(){ return java.util.logging.Logger.getGlobal(); }
	}

	@BeforeAll
	static void registerDriver() throws SQLException{
		driver = new FakeDriver();
		DriverManager.registerDriver(driver);
	}

	@AfterAll
	static void unregisterDriver() throws SQLException{
		DriverManager.deregisterDriver(driver);
	}

	@SuppressWarnings("unchecked")
	private static <T> T proxy(Class<T> type, Handler handler){
		return (T) Proxy.newProxyInstance(DatabaseManagerMappingTest.class.getClassLoader(), new Class<?>[]{type}, (p, method, args) -> {
			if(method.getName().equals("toString")) return type.getSimpleName() + "Proxy";
			return handler.handle(method.getName(), args);
		});
	}

	private interface Handler{

		Object handle(String method, Object[] args) throws Exception;
	}

	/** What the application wrote: the statement text and, per batch row, the value of every parameter. */
	private static final List<String> PREPARED = new ArrayList<>();
	private static final List<Map<Integer, Object>> ROWS = new ArrayList<>();

	private static PreparedStatement preparedStatement(String sql){
		PREPARED.add(sql);
		Map<Integer, Object> current = new TreeMap<>();
		return proxy(PreparedStatement.class, (m, a) -> switch(m){
			case "setString", "setTimestamp", "setObject" -> {
				current.put((Integer) a[0], a[1]);
				yield null;
			}
			case "setNull" -> {
				current.put((Integer) a[0], null);
				yield null;
			}
			case "addBatch" -> {
				ROWS.add(new TreeMap<>(current));
				current.clear();
				yield null;
			}
			case "executeBatch" -> new int[ROWS.size()];
			case "close", "clearBatch" -> null;
			default -> throw new UnsupportedOperationException("PreparedStatement." + m);
		});
	}

	private static Statement statement(){
		return proxy(Statement.class, (m, a) -> switch(m){
			case "execute" -> {
				EXECUTED.add((String) a[0]);
				yield false;
			}
			case "close" -> null;
			default -> throw new UnsupportedOperationException("Statement." + m);
		});
	}

	private static DatabaseMetaData metaData(){
		return proxy(DatabaseMetaData.class, (m, a) -> {
			if(!m.equals("getColumns")){
				throw new UnsupportedOperationException("DatabaseMetaData." + m);
			}
			boolean present = EXISTING_COLUMNS.contains(a[2] + "." + a[3]);
			boolean[] consumed = {false};
			return proxy(ResultSet.class, (rm, ra) -> switch(rm){
				case "next" -> present && !consumed[0] && (consumed[0] = true);
				case "close" -> null;
				default -> throw new UnsupportedOperationException("ResultSet." + rm);
			});
		});
	}

	private static DatabaseManager manager(){
		return new DatabaseManager(new DatabaseConfig(URL, "u", "p"));
	}

	private static Set<String> columnsOfInternetDocumentsDdl(){
		String ddl = EXECUTED.stream().filter(s -> s.contains("CREATE TABLE IF NOT EXISTS nova_post_internet_documents")).findFirst().orElseThrow();
		Set<String> columns = new LinkedHashSet<>();
		Matcher m = Pattern.compile("(?m)^\\s*([a-z_]+)\\s+(?:VARCHAR|DATETIME|TIMESTAMP)").matcher(ddl);
		while(m.find()){
			columns.add(m.group(1));
		}
		return columns;
	}

	@Test
	void mappingReadsOnlyColumnsThatTheSchemaCreates() throws Exception{
		EXECUTED.clear();
		EXISTING_COLUMNS.clear();
		manager().initializeTables();
		Set<String> columns = columnsOfInternetDocumentsDdl();
		assertTrue(columns.containsAll(Set.of("ref", "int_doc_number", "date_time", "cost_on_site", "declared_cost", "estimated_delivery_date")), columns.toString());

		Map<String, Object> row = new HashMap<>();
		row.put("ref", "r");
		row.put("int_doc_number", "2040");
		row.put("cost_on_site", "55");
		row.put("declared_cost", "1500");
		row.put("state_name", "Прибув");
		List<String> asked = new ArrayList<>();
		ResultSet rs = proxy(ResultSet.class, (m, a) -> {
			String column = (String) a[0];
			asked.add(column);
			if(!columns.contains(column)){
				throw new SQLException("Column '" + column + "' not found.");
			}
			return switch(m){
				case "getString" -> row.get(column);
				case "getTimestamp" -> null;
				default -> throw new UnsupportedOperationException("ResultSet." + m);
			};
		});

		InternetDocumentListItem item = manager().mapInternetDocument(rs);

		assertEquals(new BigDecimal("55"), item.costOnSite(), "delivery price");
		assertEquals(new BigDecimal("1500"), item.cost(), "declared value");
		assertEquals("Прибув", item.stateName());
		assertTrue(columns.containsAll(asked), "reads columns that do not exist: " + asked);
	}

	@Test
	void typedValuesAreWrittenAsTheTextTheColumnsHaveAlwaysHeld() throws Exception{
		var mapper = com.novapost.client.NovaPostClient.createDefaultMapper();
		PREPARED.clear();
		ROWS.clear();

		manager().insertWarehouses(List.of(mapper.readValue("{\"Ref\":\"w1\",\"Description\":\"d\",\"Longitude\":\"30.523541000000000\",\"Latitude\":50.450418,"
				+ "\"TotalMaxWeightAllowed\":\"0\",\"PlaceMaxWeightAllowed\":1000,\"POSTerminal\":\"1\"}", com.novapost.model.Warehouse.class)));
		Map<Integer, Object> warehouse = ROWS.getLast();
		assertEquals("30.523541000000000", warehouse.get(14), "longitude keeps the scale the API sent");
		assertEquals("50.450418", warehouse.get(15), "latitude");
		assertEquals("0", warehouse.get(16), "total max weight");
		assertEquals("1000", warehouse.get(17), "place max weight");

		ROWS.clear();
		manager().insertSettlements(List.of(mapper.readValue("{\"Ref\":\"s1\",\"Description\":\"d\",\"Latitude\":\"50.4504180000000\",\"Longitude\":30.52,\"Warehouse\":\"1\"}",
				com.novapost.model.Settlement.class), mapper.readValue("{\"Ref\":\"s2\",\"Description\":\"d\",\"Warehouse\":0}", com.novapost.model.Settlement.class),
				mapper.readValue("{\"Ref\":\"s3\",\"Description\":\"d\"}", com.novapost.model.Settlement.class)));
		assertEquals("50.4504180000000", ROWS.get(0).get(10), "latitude");
		assertEquals("30.52", ROWS.get(0).get(11), "longitude");
		assertEquals("1", ROWS.get(0).get(15), "has branches: the flag is written as the column has always held it");
		assertEquals("0", ROWS.get(1).get(15));
		assertNull(ROWS.get(2).get(15), "unknown stays unknown");

		ROWS.clear();
		manager().insertInternetDocuments(List.of(mapper.readValue("{\"Ref\":\"d1\",\"IntDocNumber\":\"2040\",\"CostOnSite\":\"85.00\",\"Cost\":15000,\"Weight\":\"1.5\",\"SeatsAmount\":\"2\"}",
				InternetDocumentListItem.class)));
		Map<Integer, Object> document = ROWS.getLast();
		assertEquals("85.00", document.get(4), "delivery price");
		assertEquals("15000", document.get(5), "declared value");
		assertEquals("1.5", document.get(6), "weight");
		assertEquals("2", document.get(7), "seats");
	}

	@Test
	void oldTablesAreMigrated() throws Exception{
		EXECUTED.clear();
		EXISTING_COLUMNS.clear();
		EXISTING_COLUMNS.add("nova_post_internet_documents.cost"); // the pre-fix schema: one "cost" column holding the delivery price

		manager().initializeTables();

		assertTrue(EXECUTED.stream().anyMatch(s -> s.contains("CHANGE COLUMN cost cost_on_site")), EXECUTED.toString());
		assertTrue(EXECUTED.stream().anyMatch(s -> s.contains("ADD COLUMN declared_cost")), EXECUTED.toString());
	}

	@Test
	void currentTablesAreLeftAlone() throws Exception{
		EXECUTED.clear();
		EXISTING_COLUMNS.clear();
		EXISTING_COLUMNS.add("nova_post_internet_documents.cost_on_site");
		EXISTING_COLUMNS.add("nova_post_internet_documents.declared_cost");

		manager().initializeTables();

		assertTrue(EXECUTED.stream().noneMatch(s -> s.startsWith("ALTER TABLE")), EXECUTED.toString());
	}
}
