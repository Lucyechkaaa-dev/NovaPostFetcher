package com.novapost.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class ModelProjector {

	private final ObjectMapper objectMapper;
	private final ObjectMapper projectionMapper;

	public ModelProjector() {
		this(NovaPostClient.createDefaultMapper());
	}

	public ModelProjector(ObjectMapper objectMapper) {
		this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
		this.projectionMapper = createProjectionMapper(this.objectMapper);
	}

	private static ObjectMapper createProjectionMapper(ObjectMapper base) {
		return com.fasterxml.jackson.databind.json.JsonMapper.builder()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
				.addModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module())
				.addModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
				.build();
	}

	private static final ModelProjector DEFAULT_INSTANCE = new ModelProjector();

	public static ModelProjector getInstance() {
		return DEFAULT_INSTANCE;
	}

	public Map<String, Object> projectJsonToMap(JsonNode node, Map<String, Object> targetFields) {
		if (node == null || !node.isObject() || targetFields == null) {
			return Collections.emptyMap();
		}

		Map<String, JsonNode> fieldLookup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		node.fields().forEachRemaining(entry -> fieldLookup.put(entry.getKey(), entry.getValue()));

		Map<String, Object> result = new LinkedHashMap<>();
		for (String targetKey : targetFields.keySet()) {
			if (targetKey == null) {
				continue;
			}
			JsonNode valNode = fieldLookup.get(targetKey.trim());
			result.put(targetKey, jsonNodeToObject(valNode));
		}

		return result;
	}

	public Map<String, Object> projectJsonToMap(String json, Map<String, Object> targetFields) {
		if (json == null || json.isBlank() || targetFields == null) {
			return Collections.emptyMap();
		}
		try {
			JsonNode node = objectMapper.readTree(json);
			return projectJsonToMap(node, targetFields);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse JSON string: " + e.getMessage(), e);
		}
	}

	public List<Map<String, Object>> projectJsonArrayToMap(JsonNode arrayNode, Map<String, Object> targetFields) {
		if (arrayNode == null || !arrayNode.isArray() || targetFields == null) {
			return Collections.emptyList();
		}
		List<Map<String, Object>> list = new ArrayList<>(arrayNode.size());
		for (JsonNode itemNode : arrayNode) {
			if (itemNode != null && itemNode.isObject()) {
				list.add(projectJsonToMap(itemNode, targetFields));
			}
		}
		return Collections.unmodifiableList(list);
	}

	public List<Map<String, Object>> projectJsonArrayToMap(String jsonArray, Map<String, Object> targetFields) {
		if (jsonArray == null || jsonArray.isBlank() || targetFields == null) {
			return Collections.emptyList();
		}
		try {
			JsonNode node = objectMapper.readTree(jsonArray);
			return projectJsonArrayToMap(node, targetFields);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse JSON array string: " + e.getMessage(), e);
		}
	}

	public <T> T projectJsonToClass(JsonNode node, Class<T> targetType) {
		if (node == null || node.isNull() || node.isMissingNode() || targetType == null) {
			return null;
		}
		try {
			return projectionMapper.treeToValue(node, targetType);
		} catch (Exception e) {
			Map<String, Object> sourceLookup = buildNormalizedSourceLookupFromJson(node);
			try {
				if (targetType.isRecord()) {
					return instantiateRecord(targetType, sourceLookup);
				} else {
					return instantiatePojo(targetType, sourceLookup);
				}
			} catch (Exception ex) {
				throw new IllegalArgumentException("Failed to project JSON node to " + targetType.getName() + ": " + ex.getMessage(), ex);
			}
		}
	}

	public <T> T projectJsonToClass(String json, Class<T> targetType) {
		if (json == null || json.isBlank() || targetType == null) {
			return null;
		}
		try {
			JsonNode node = objectMapper.readTree(json);
			return projectJsonToClass(node, targetType);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse JSON string for " + targetType.getName() + ": " + e.getMessage(), e);
		}
	}

	public <T> List<T> projectJsonArrayToClass(JsonNode arrayNode, Class<T> targetType) {
		if (arrayNode == null || !arrayNode.isArray() || targetType == null) {
			return Collections.emptyList();
		}
		List<T> list = new ArrayList<>(arrayNode.size());
		for (JsonNode itemNode : arrayNode) {
			if (itemNode != null && itemNode.isObject()) {
				list.add(projectJsonToClass(itemNode, targetType));
			}
		}
		return Collections.unmodifiableList(list);
	}

	public <T> List<T> projectJsonArrayToClass(String jsonArray, Class<T> targetType) {
		if (jsonArray == null || jsonArray.isBlank() || targetType == null) {
			return Collections.emptyList();
		}
		try {
			JsonNode node = objectMapper.readTree(jsonArray);
			return projectJsonArrayToClass(node, targetType);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse JSON array string for " + targetType.getName() + ": " + e.getMessage(), e);
		}
	}

	public Map<String, Object> projectToMap(Object source, Map<String, Object> targetFields) {
		if (source == null || targetFields == null) {
			return Collections.emptyMap();
		}
		if (source instanceof JsonNode node) {
			return projectJsonToMap(node, targetFields);
		}
		if (source instanceof String str && str.trim().startsWith("{")) {
			return projectJsonToMap(str, targetFields);
		}

		Map<String, Object> sourceLookup = buildNormalizedSourceLookup(source);
		Map<String, Object> result = new LinkedHashMap<>();

		for (String targetKey : targetFields.keySet()) {
			if (targetKey == null) {
				continue;
			}
			Object value = sourceLookup.get(targetKey.trim());
			result.put(targetKey, value);
		}

		return result;
	}

	public List<Map<String, Object>> projectAllToMap(Iterable<?> sources, Map<String, Object> targetFields) {
		if (sources == null || targetFields == null) {
			return Collections.emptyList();
		}
		if (sources instanceof JsonNode arrayNode && arrayNode.isArray()) {
			return projectJsonArrayToMap(arrayNode, targetFields);
		}
		List<Map<String, Object>> list = new ArrayList<>();
		for (Object src : sources) {
			if (src != null) {
				list.add(projectToMap(src, targetFields));
			}
		}
		return Collections.unmodifiableList(list);
	}

	public <T> T projectToClass(Object source, Class<T> targetType) {
		if (source == null || targetType == null) {
			return null;
		}
		if (source instanceof JsonNode node) {
			return projectJsonToClass(node, targetType);
		}
		if (source instanceof String str && str.trim().startsWith("{")) {
			return projectJsonToClass(str, targetType);
		}

		Map<String, Object> sourceLookup = buildNormalizedSourceLookup(source);

		try {
			if (targetType.isRecord()) {
				return instantiateRecord(targetType, sourceLookup);
			} else {
				return instantiatePojo(targetType, sourceLookup);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to project source object to " + targetType.getName() + ": " + e.getMessage(), e);
		}
	}

	public <T> List<T> projectAllToClass(Iterable<?> sources, Class<T> targetType) {
		if (sources == null || targetType == null) {
			return Collections.emptyList();
		}
		if (sources instanceof JsonNode arrayNode && arrayNode.isArray()) {
			return projectJsonArrayToClass(arrayNode, targetType);
		}
		List<T> list = new ArrayList<>();
		for (Object src : sources) {
			if (src != null) {
				list.add(projectToClass(src, targetType));
			}
		}
		return Collections.unmodifiableList(list);
	}

	private Object jsonNodeToObject(JsonNode node) {
		if (node == null || node.isNull() || node.isMissingNode()) {
			return null;
		}
		if (node.isTextual()) {
			return node.asText();
		}
		if (node.isBoolean()) {
			return node.asBoolean();
		}
		if (node.isInt()) {
			return node.asInt();
		}
		if (node.isLong()) {
			return node.asLong();
		}
		if (node.isDouble() || node.isFloat()) {
			return node.asDouble();
		}
		if (node.isNumber()) {
			return node.numberValue();
		}
		try {
			return objectMapper.treeToValue(node, Object.class);
		} catch (Exception ignored) {
			return node.toString();
		}
	}

	private Map<String, Object> buildNormalizedSourceLookupFromJson(JsonNode node) {
		Map<String, Object> lookup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		if (node != null && node.isObject()) {
			node.fields().forEachRemaining(entry -> lookup.put(entry.getKey(), jsonNodeToObject(entry.getValue())));
		}
		return lookup;
	}

	private Map<String, Object> buildNormalizedSourceLookup(Object source) {
		Map<String, Object> lookup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		if (source instanceof Map<?, ?> map) {
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				if (entry.getKey() != null) {
					lookup.put(entry.getKey().toString().trim(), entry.getValue());
				}
			}
			return lookup;
		}

		Class<?> sourceClass = source.getClass();

		if (sourceClass.isRecord()) {
			for (RecordComponent rc : sourceClass.getRecordComponents()) {
				try {
					Object value = rc.getAccessor().invoke(source);
					lookup.put(rc.getName(), value);
					JsonProperty ann = getJsonProperty(sourceClass, rc);
					if (ann != null && !ann.value().isBlank()) {
						lookup.put(ann.value(), value);
					}
				} catch (Exception ignored) {
				}
			}
		} else {
			for (Field field : getAllFields(sourceClass)) {
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				try {
					Object value = field.get(source);
					lookup.put(field.getName(), value);
					JsonProperty ann = getJsonProperty(sourceClass, field);
					if (ann != null && !ann.value().isBlank()) {
						lookup.put(ann.value(), value);
					}
				} catch (Exception ignored) {
				}
			}

			for (Method method : sourceClass.getMethods()) {
				if (Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 0) {
					continue;
				}
				String name = method.getName();
				String propName = null;
				if (name.startsWith("get") && name.length() > 3) {
					propName = Character.toLowerCase(name.charAt(3)) + name.substring(4);
				} else if (name.startsWith("is") && name.length() > 2) {
					propName = Character.toLowerCase(name.charAt(2)) + name.substring(3);
				}
				if (propName != null) {
					try {
						Object value = method.invoke(source);
						lookup.putIfAbsent(propName, value);
						JsonProperty ann = method.getAnnotation(JsonProperty.class);
						if (ann != null && !ann.value().isBlank()) {
							lookup.putIfAbsent(ann.value(), value);
						}
					} catch (Exception ignored) {
					}
				}
			}
		}

		return lookup;
	}

	@SuppressWarnings("unchecked")
	private <T> T instantiateRecord(Class<T> recordClass, Map<String, Object> sourceLookup) throws Exception {
		RecordComponent[] components = recordClass.getRecordComponents();
		Class<?>[] paramTypes = new Class<?>[components.length];
		Object[] args = new Object[components.length];

		for (int i = 0; i < components.length; i++) {
			RecordComponent rc = components[i];
			paramTypes[i] = rc.getType();

			String name = rc.getName();
			Object rawVal = sourceLookup.get(name);
			if (rawVal == null) {
				JsonProperty ann = getJsonProperty(recordClass, rc);
				if (ann != null && !ann.value().isBlank()) {
					rawVal = sourceLookup.get(ann.value());
				}
			}

			args[i] = convertValue(rawVal, rc.getType());
		}

		Constructor<T> canonicalConstructor = recordClass.getDeclaredConstructor(paramTypes);
		canonicalConstructor.setAccessible(true);
		return canonicalConstructor.newInstance(args);
	}

	private <T> T instantiatePojo(Class<T> pojoClass, Map<String, Object> sourceLookup) throws Exception {
		Constructor<T> noArgConstructor = pojoClass.getDeclaredConstructor();
		noArgConstructor.setAccessible(true);
		T instance = noArgConstructor.newInstance();

		List<Field> fields = getAllFields(pojoClass);
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
				continue;
			}

			String name = field.getName();
			Object rawVal = sourceLookup.get(name);
			if (rawVal == null) {
				JsonProperty ann = getJsonProperty(pojoClass, field);
				if (ann != null && !ann.value().isBlank()) {
					rawVal = sourceLookup.get(ann.value());
				}
			}

			if (rawVal == null) {
				continue;
			}

			Object converted = convertValue(rawVal, field.getType());
			Method setter = findSetter(pojoClass, field);
			if (setter != null) {
				setter.invoke(instance, converted);
			} else {
				field.setAccessible(true);
				field.set(instance, converted);
			}
		}

		return instance;
	}

	private JsonProperty getJsonProperty(Class<?> clazz, RecordComponent rc) {
		JsonProperty ann = rc.getAnnotation(JsonProperty.class);
		if (ann != null) {
			return ann;
		}
		if (rc.getAccessor() != null) {
			ann = rc.getAccessor().getAnnotation(JsonProperty.class);
			if (ann != null) {
				return ann;
			}
		}
		try {
			Field f = clazz.getDeclaredField(rc.getName());
			ann = f.getAnnotation(JsonProperty.class);
			if (ann != null) {
				return ann;
			}
		} catch (NoSuchFieldException ignored) {
		}
		return null;
	}

	private JsonProperty getJsonProperty(Class<?> clazz, Field field) {
		JsonProperty ann = field.getAnnotation(JsonProperty.class);
		if (ann != null) {
			return ann;
		}
		String name = field.getName();
		String getterName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		try {
			Method getter = clazz.getMethod(getterName);
			ann = getter.getAnnotation(JsonProperty.class);
			if (ann != null) {
				return ann;
			}
		} catch (NoSuchMethodException ignored) {
		}
		return null;
	}

	private Method findSetter(Class<?> clazz, Field field) {
		String propName = field.getName();
		String setterName = "set" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
				return method;
			}
		}
		return null;
	}

	private List<Field> getAllFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();
		Class<?> current = clazz;
		while (current != null && current != Object.class) {
			Collections.addAll(fields, current.getDeclaredFields());
			current = current.getSuperclass();
		}
		return fields;
	}

	private Object convertValue(Object value, Class<?> targetType) {
		if (value == null) {
			if (targetType.isPrimitive()) {
				if (targetType == boolean.class) return false;
				if (targetType == byte.class) return (byte) 0;
				if (targetType == short.class) return (short) 0;
				if (targetType == int.class) return 0;
				if (targetType == long.class) return 0L;
				if (targetType == float.class) return 0.0f;
				if (targetType == double.class) return 0.0d;
				if (targetType == char.class) return '\0';
			}
			return null;
		}

		if (targetType.isInstance(value)) {
			return value;
		}

		try {
			return projectionMapper.convertValue(value, targetType);
		} catch (Exception ignored) {
			if (targetType == String.class) {
				return value.toString();
			}
			return null;
		}
	}
}
