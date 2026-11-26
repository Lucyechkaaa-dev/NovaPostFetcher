package com.novapost;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.client.NpModelStyle;
import com.novapost.testsupport.ApiSpec;
import com.novapost.testsupport.ApiSpec.Binding;
import com.novapost.testsupport.RecordingHttpClient;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks the code against the documentation snapshot in {@code novaposhta/api-spec.txt}:
 * every documented page and field is present, and every documented method is reachable through a facade and is sent
 * with the documented {@code modelName}/{@code calledMethod}.
 */
class ApiSpecConformanceTest{

	/** The 85 JSON method pages of https://developers.novaposhta.ua/documentation (first 8 characters of each page id). */
	static final Set<String> DOCUMENTED_PAGES = Set.of(
			// CarCallGeneral (8)
			"6ed8c0ea", "97e2097b", "730e80bc", "d5efafc8", "edfb5c7d", "ee1ff6d3", "ee23f404", "ee2a2001",
			// AddressGeneral (13)
			"a0eb83ab", "a1329635", "a155d0d9", "a177069a", "a19ba934", "a1c42723", "a1e6f0a7", "a20ee6e4", "a2322f38",
			"a2587b53", "a27c20d7", "a98072f9", "c0bfb1a3",
			// CounterpartyGeneral (9)
			"0ae5dd75", "b0fdf818", "bc3c44c7", "a2c3c708", "a2eb27e8", "a30dbb7c", "a332efbf", "a3575a67", "a37a06df",
			// ContactPersonGeneral (3)
			"a3a25bda", "a3c5a577", "a3ea91c8",
			// ScanSheetGeneral (5)
			"a482293c", "a4abdd36", "a4d93a89", "a50e049b", "a53dea8a",
			// CommonGeneral (13)
			"a56d5c1c", "a5912a1e", "a5b46873", "a5dd575e", "a6247f2f", "a6492db4", "a66fada0", "a697db47", "a6bce5a1",
			"a6e189f7", "a754ff0d", "affdad3e", "c4389a2c",
			// AdditionalServiceGeneral (18)
			"a8d29fc2", "98acb0f6", "1934683d", "c61d3597", "a8faa2e6", "db0e1b90", "a85bb34b", "a778f519", "a7fb4a3a",
			"06b78d02", "2c81f3bc", "a82d087c", "4ee79a8d", "a7a6bacb", "a7cb69ee", "a8ad1dc7", "a886b776", "c09f1b02",
			// InternetDocumentGeneral (15)
			"a91f115b", "a941c714", "a965630e", "0227072e", "3fdc36f1", "751067b8", "a19ced79", "c7c8aaf5", "df942798",
			"f74a0918", "a98a4354", "a9d22b34", "a9f43ff1", "aa1b448f", "dfc923e9",
			// TrackingDocumentGeneral (1)
			"a9ae7bc9");

	private static final List<Binding> BINDINGS = ApiSpec.bindings();

	private static Set<String> jsonNames(List<Class<?>> classes){
		Set<String> names = new HashSet<>();
		for(Class<?> c : classes){
			assertTrue(c.isRecord(), c + " is not a record");
			for(RecordComponent rc : c.getRecordComponents()){
				// @JsonProperty is not a RECORD_COMPONENT annotation: it ends up on the record's field
				try{
					JsonProperty p = c.getDeclaredField(rc.getName()).getAnnotation(JsonProperty.class);
					if(p != null){
						names.add(p.value());
					}
				}
				catch(NoSuchFieldException e){
					throw new AssertionError(e);
				}
			}
		}
		return names;
	}

	@Test
	void everyDocumentedPageIsInTheSpec(){
		Set<String> pages = new TreeSet<>();
		BINDINGS.forEach(b -> pages.addAll(b.docs()));

		assertEquals(85, DOCUMENTED_PAGES.size(), "the expected-page constant itself must list 85 pages");
		assertEquals(new TreeSet<>(DOCUMENTED_PAGES), pages, "pages missing from the spec or unknown ones present");
	}

	@Test
	void sixtyEightDistinctMethodsAreCovered(){
		Set<String> distinct = new TreeSet<>();
		BINDINGS.forEach(b -> distinct.add(b.model() + "/" + b.method()));

		assertEquals(68, distinct.size(), distinct.toString());
		assertEquals(75, BINDINGS.size(), "one binding per facade method");
	}

	@Test
	void everyDocumentedFieldExistsInTheJavaModels(){
		List<String> problems = new ArrayList<>();
		int requestFields = 0, responseFields = 0;
		for(Binding b : BINDINGS){
			Set<String> req = jsonNames(b.reqClasses());
			Set<String> res = jsonNames(b.resClasses());
			for(String n : b.reqNames()){
				requestFields++;
				if(!req.contains(n)) problems.add(b.model() + "/" + b.method() + " request is missing " + n);
			}
			for(String n : b.resNames()){
				responseFields++;
				if(!res.contains(n)) problems.add(b.model() + "/" + b.method() + " response is missing " + n);
			}
			for(String n : b.reqExtras()){
				if(!req.contains(n)) problems.add(b.model() + "/" + b.method() + " request lost the extra field " + n);
			}
			for(String n : b.resExtras()){
				if(!res.contains(n)) problems.add(b.model() + "/" + b.method() + " response lost the extra field " + n);
			}
		}
		System.out.println("Spec conformance: " + BINDINGS.size() + " bindings, " + requestFields + " documented request fields, "
				+ responseFields + " documented response fields checked");
		assertTrue(problems.isEmpty(), String.join("\n", problems));
		assertTrue(requestFields >= 350 && responseFields >= 750, "suspiciously few fields checked: " + requestFields + "/" + responseFields);
	}

	/** What each type token of the spec ({@code Name:type}) means in Java. A token with another type is a plain Java type and is not checked. */
	private static final Map<String, Class<?>> TOKEN_TYPES = Map.ofEntries(
			Map.entry("date", java.time.LocalDate.class), Map.entry("datetime", java.time.LocalDateTime.class),
			Map.entry("time", java.time.LocalTime.class), Map.entry("dec", java.math.BigDecimal.class),
			Map.entry("int", Integer.class), Map.entry("long", Long.class), Map.entry("bool", Boolean.class),
			Map.entry("flag", Boolean.class), Map.entry("tbool", Boolean.class), Map.entry("dbl", Double.class),
			Map.entry("json", com.fasterxml.jackson.databind.JsonNode.class), Map.entry("strs", List.class));

	private static Class<?> componentType(List<Class<?>> classes, String jsonName){
		for(Class<?> c : classes){
			for(RecordComponent rc : c.getRecordComponents()){
				try{
					JsonProperty p = c.getDeclaredField(rc.getName()).getAnnotation(JsonProperty.class);
					if(p != null && p.value().equals(jsonName)){
						return rc.getType();
					}
				}
				catch(NoSuchFieldException e){
					throw new AssertionError(e);
				}
			}
		}
		return null;
	}

	@Test
	void everyTypedTokenOfTheSpecIsThatTypeInTheJavaModels(){
		List<String> problems = new ArrayList<>();
		Map<String, Integer> byToken = new TreeMap<>();
		int checked = 0;
		for(Binding b : BINDINGS){
			for(boolean request : new boolean[]{true, false}){
				Map<String, String> types = request ? b.reqTypes() : b.resTypes();
				List<Class<?>> classes = request ? b.reqClasses() : b.resClasses();
				for(Map.Entry<String, String> e : types.entrySet()){
					String token = e.getValue().endsWith("!") ? e.getValue().substring(0, e.getValue().length() - 1) : e.getValue();
					Class<?> expected = TOKEN_TYPES.get(token);
					if(expected == null){
						continue;
					}
					Class<?> actual = componentType(classes, e.getKey());
					checked++;
					byToken.merge(token, 1, Integer::sum);
					if(actual != expected){
						problems.add(b.model() + "/" + b.method() + (request ? " request " : " response ") + e.getKey() + ":" + e.getValue()
								+ " must be " + expected.getSimpleName() + " but the model has " + (actual == null ? "no such field" : actual.getSimpleName()));
					}
				}
			}
		}
		System.out.println("Spec types: " + checked + " typed tokens checked " + byToken);
		assertTrue(problems.isEmpty(), String.join("\n", problems));
		assertTrue(checked >= 250, "suspiciously few typed tokens checked: " + checked);
	}

	@Test
	void everyMethodIsRoutedToTheDocumentedModelAndMethodInBothStyles() throws Exception{
		for(NpModelStyle style : NpModelStyle.values()){
			RecordingHttpClient http = RecordingHttpClient.ok();
			NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, http, NovaPostClient.createDefaultMapper(), style);
			for(Binding b : BINDINGS){
				Class<?> apiClass = Class.forName("com.novapost.client.api." + b.api());
				Object facade = apiClass.getConstructor(NovaPostClient.class).newInstance(client);
				Method m = Arrays.stream(apiClass.getMethods())
						.filter(x -> x.getName().equals(b.java()))
						.findFirst()
						.orElseThrow(() -> new AssertionError(b.api() + " has no method " + b.java()));
				assertEquals(b.hasReq() ? 1 : 0, m.getParameterCount(), b.api() + "." + b.java() + " parameter count");
				NpCall<?> call = (NpCall<?>) (m.getParameterCount() == 0 ? m.invoke(facade) : m.invoke(facade, (Object) null));
				call.response();

				NpModel npModel = NpModel.valueOf(b.npModel());
				var sent = http.lastRequest();
				assertEquals(npModel.nameFor(style), sent.path("modelName").asText(), b.api() + "." + b.java() + " [" + style + "]");
				assertEquals(b.method(), sent.path("calledMethod").asText(), b.api() + "." + b.java());
				assertEquals("test-key", sent.path("apiKey").asText());
				if(style == NpModelStyle.DOCUMENTED){
					assertEquals(b.model(), sent.path("modelName").asText(), "the documented style must send the model name used in the docs: " + b.java());
				}
			}
			assertEquals(BINDINGS.size(), http.calls());
		}
	}
}
