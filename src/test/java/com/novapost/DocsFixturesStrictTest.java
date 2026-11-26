package com.novapost;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.novapost.client.NovaPostClient;
import com.novapost.model.NpResponse;
import com.novapost.testsupport.ApiSpec;
import com.novapost.testsupport.ApiSpec.Binding;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * The examples copied from the documentation ({@code novaposhta/examples}) against the models:
 * <ul>
 *   <li>an answer must deserialize with {@code FAIL_ON_UNKNOWN_PROPERTIES} on and every class-level {@code ignoreUnknown} switched
 *       off, so a field that the documentation shows but the model does not declare fails the test (models that keep unknown
 *       fields in an {@code extra} map must leave it empty);</li>
 *   <li>a request must survive parse (strict) and serialize unchanged.</li>
 * </ul>
 */
class DocsFixturesStrictTest{

	private static final Path DIR = examplesDir();
	private static final ObjectMapper STRICT = strictMapper();
	private static final ObjectMapper MAPPER = NovaPostClient.createDefaultMapper();

	private static Path examplesDir(){
		try{
			return Path.of(DocsFixturesStrictTest.class.getResource("/novaposhta/examples").toURI());
		}
		catch(Exception e){
			throw new IllegalStateException("fixtures directory not found", e);
		}
	}

	/** Fails on any field a model does not declare: the class-level {@code @JsonIgnoreProperties(ignoreUnknown = true)} is ignored. */
	static ObjectMapper strictMapper(){
		ObjectMapper mapper = NovaPostClient.createDefaultMapper();
		mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector(){
			@Override
			public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config, Annotated ann){
				return JsonIgnoreProperties.Value.empty();
			}
		});
		mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// the client mapper reads a value it cannot convert as null; the documentation tests must see such a value fail
		mapper.clearProblemHandlers();
		return mapper;
	}

	private static List<String> pageIds(String suffix) throws IOException{
		try(Stream<Path> files = Files.list(DIR)){
			return files.map(p -> p.getFileName().toString()).filter(n -> n.endsWith(suffix))
					.map(n -> n.substring(0, n.indexOf('.'))).sorted().toList();
		}
	}

	private static Binding binding(String pageId){
		return ApiSpec.byPage(pageId).orElseThrow(() -> new AssertionError("no spec binding for documentation page " + pageId));
	}

	private static String label(String pageId){
		Binding b = binding(pageId);
		return b.model() + "/" + b.method() + " (" + b.java() + ")";
	}

	/** Names the documentation page in every failure: a dynamic test index alone says nothing. */
	private static void onPage(String id, org.junit.jupiter.api.function.Executable body){
		try{
			body.execute();
		}
		catch(Throwable t){
			String message = String.valueOf(t.getMessage()).split("\n")[0];
			throw new AssertionError("page " + id + " " + label(id) + ": " + t.getClass().getSimpleName() + ": " + message, t);
		}
	}

	@TestFactory
	Stream<DynamicTest> responseExamplesDeserializeWithoutUnknownFields() throws IOException{
		return pageIds(".response.json").stream().map(id -> dynamicTest(id + " " + label(id), () -> onPage(id, () -> {
			Class<?> item = binding(id).resClasses().getFirst();
			JavaType type = STRICT.getTypeFactory().constructParametricType(NpResponse.class, item);

			NpResponse<?> response = STRICT.readValue(Files.readString(DIR.resolve(id + ".response.json")), type);

			assertTrue(response.success(), "success");
			assertFalse(response.data().isEmpty(), "data");
			for(Object element : response.data()){
				assertNoUndeclaredFields(element);
			}
		})));
	}

	@TestFactory
	Stream<DynamicTest> requestExamplesRoundTripThroughTheRequestModels() throws IOException{
		return pageIds(".request.json").stream().map(id -> dynamicTest(id + " " + label(id), () -> onPage(id, () -> {
			String text = Files.readString(DIR.resolve(id + ".request.json"));
			if(binding(id).reqClasses().isEmpty()){
				// the spec says the method takes no parameters, so the documented example must not show any
				assertEquals(0, MAPPER.readTree(text).size(), "the example shows parameters although the spec lists none");
				return;
			}
			Class<?> request = binding(id).reqClasses().getFirst();

			Object parsed = STRICT.readValue(text, request);
			JsonNode actual = MAPPER.readTree(MAPPER.writeValueAsString(parsed));

			List<String> differences = new ArrayList<>();
			compare("$", MAPPER.readTree(text), actual, differences);
			assertTrue(differences.isEmpty(), request.getSimpleName() + " does not reproduce the documented example:\n" + String.join("\n", differences));
		})));
	}

	@Test
	void fixtureCoverageIsAsRecorded() throws IOException{
		List<String> requests = pageIds(".request.json");
		List<String> responses = pageIds(".response.json");
		Set<String> pages = new TreeSet<>(requests);
		pages.addAll(responses);

		System.out.println("Docs fixtures: " + requests.size() + " request + " + responses.size() + " response files covering "
				+ pages.size() + " of " + ApiSpecConformanceTest.DOCUMENTED_PAGES.size() + " documentation pages");
		// every documented JSON page has both of its examples
		assertEquals(85, requests.size());
		assertEquals(85, responses.size());
		assertEquals(new TreeSet<>(ApiSpecConformanceTest.DOCUMENTED_PAGES), pages);
		// the two big answers are complete: 124 InternetDocument fields and 107 TrackingStatus fields
		assertEquals(124, MAPPER.readTree(Files.readString(DIR.resolve("dfc923e9.response.json"))).path("data").get(0).size());
		assertEquals(107, MAPPER.readTree(Files.readString(DIR.resolve("a9ae7bc9.response.json"))).path("data").get(0).size());
	}

	@Test
	void theStrictMapperReallyRejectsUndeclaredFields(){
		// guards the guard: without this the tests above could pass for the wrong reason
		assertThrows(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class,
				() -> STRICT.readValue("{\"Ref\":\"r\",\"SomethingNew\":1}", com.novapost.model.InternetDocumentDeleteResponse.class));
		assertDoesNotThrow(() -> MAPPER.readValue("{\"Ref\":\"r\",\"SomethingNew\":1}", com.novapost.model.InternetDocumentDeleteResponse.class));
	}

	@Test
	void theStrictMapperReallyRejectsValuesThatCannotBeReadAsTheirType() throws Exception{
		// the same guard for typed fields: the client mapper reads such a value as null, the strict one must fail
		String json = "{\"DateTime\":\"not a date\"}";
		assertThrows(com.fasterxml.jackson.databind.JsonMappingException.class, () -> STRICT.readValue(json, com.novapost.model.InternetDocumentListItem.class));
		assertNull(MAPPER.readValue(json, com.novapost.model.InternetDocumentListItem.class).dateTime());
	}

	private static void assertNoUndeclaredFields(Object element){
		if(element == null || !element.getClass().isRecord()){
			return;
		}
		for(RecordComponent rc : element.getClass().getRecordComponents()){
			if(rc.getName().equals("extra") && Map.class.isAssignableFrom(rc.getType())){
				try{
					Map<?, ?> extra = (Map<?, ?>) rc.getAccessor().invoke(element);
					assertTrue(extra.isEmpty(), element.getClass().getSimpleName() + " received fields it does not declare: " + extra.keySet());
				}
				catch(ReflectiveOperationException e){
					throw new AssertionError(e);
				}
			}
		}
	}

	/** Same object keys and array lengths; scalars compare by their text because the API (and the documentation) quote numbers. */
	private static void compare(String path, JsonNode expected, JsonNode actual, List<String> out){
		if(expected.isObject() && actual.isObject()){
			Set<String> keys = new TreeSet<>();
			expected.fieldNames().forEachRemaining(keys::add);
			actual.fieldNames().forEachRemaining(keys::add);
			for(String k : keys){
				if(!expected.has(k)) out.add(path + "." + k + " appears only after the round trip");
				else if(!actual.has(k)){
					// an empty string in the example means "not set"; a model that leaves such a parameter out says the same. The key must still
					// be declared: the strict parse above rejects a field the model does not know.
					boolean blank = expected.get(k).isTextual() && expected.get(k).asText().isEmpty();
					if(!blank) out.add(path + "." + k + " was lost");
				}
				else compare(path + "." + k, expected.get(k), actual.get(k), out);
			}
		}
		else if(expected.isArray() && actual.isArray()){
			if(expected.size() != actual.size()){
				out.add(path + " has " + actual.size() + " elements instead of " + expected.size());
				return;
			}
			for(int i = 0; i < expected.size(); i++){
				compare(path + "[" + i + "]", expected.get(i), actual.get(i), out);
			}
		}
		else if(expected.isContainerNode() || actual.isContainerNode() || !expected.asText().equals(actual.asText())){
			out.add(path + ": expected " + expected + " but was " + actual);
		}
	}
}
