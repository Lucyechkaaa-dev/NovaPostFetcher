import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates request/response records (with builders for requests) and the per-section facades
 * from the API spec.
 *
 * <pre>
 * java tools/ModelGenerator.java src/test/resources/novaposhta/api-spec.txt src/main/java
 * </pre>
 *
 * A generated file that contains the line comment {@code // @keep} is never overwritten, so a record can be
 * generated once and then extended by hand.
 */
public class ModelGenerator{

	private static final String DOCS = "https://developers.novaposhta.ua/view/model/";
	private static final Set<String> KEYWORDS = Set.of("new", "class", "default", "package", "interface", "enum", "switch",
			"case", "public", "private", "static", "final", "void", "int", "long", "short", "byte", "char", "float", "double",
			"boolean", "import", "return", "this", "super", "throw", "throws", "try", "catch", "finally", "for", "while", "do",
			"if", "else", "goto", "const", "abstract", "assert", "break", "continue", "extends", "implements", "instanceof",
			"native", "protected", "strictfp", "synchronized", "transient", "volatile", "null", "true", "false");
	private static final Pattern TOKEN = Pattern.compile("(\\+?)([A-Za-z][A-Za-z0-9]*)(?:=([A-Za-z][A-Za-z0-9]*))?(?::([^~]+))?(?:~(.+))?");

	record Tok(String json, String java, String type, boolean extra, String dflt){

	}

	static final class Cls{
		final String fqn;
		final boolean request;
		final Map<String, Tok> fields = new LinkedHashMap<>();
		final Set<String> usedBy = new LinkedHashSet<>();
		String section = "";

		Cls(String fqn, boolean request){
			this.fqn = fqn;
			this.request = request;
		}
	}

	static final class Side{
		final List<String> refs = new ArrayList<>();
		boolean none = true;
	}

	static final class Binding{
		String model, method, java, npModel, docs;
		final Side req = new Side(), res = new Side();
		final List<String> notes = new ArrayList<>();
	}

	static final class Api{
		final String name, uuid, defaultModel;
		final List<Binding> bindings = new ArrayList<>();

		Api(String name, String uuid, String defaultModel){
			this.name = name;
			this.uuid = uuid;
			this.defaultModel = defaultModel;
		}
	}

	public static void main(String[] args) throws IOException{
		Path spec = Path.of(args[0]);
		Path out = Path.of(args[1]);
		Map<String, Cls> classes = new LinkedHashMap<>();
		Set<String> withExtra = new LinkedHashSet<>();
		List<Api> apis = new ArrayList<>();
		String pkg = "com.novapost.model";
		Api api = null;
		Binding binding = null;

		for(String raw : Files.readAllLines(spec)){
			String line = raw.strip();
			if(line.isEmpty() || line.startsWith("#")){
				continue;
			}
			if(line.startsWith("@pkg ")){
				pkg = line.substring(5).strip();
			}
			else if(line.startsWith("@api ")){
				String[] p = line.split("\\s+");
				api = new Api(p[1], p[2], p[3]);
				apis.add(api);
			}
			else if(line.startsWith("extra ")){
				// extra <Class>: keep the fields the model does not declare in a Map (@JsonAnySetter)
				withExtra.add(resolve(line.substring(6).strip(), pkg));
			}
			else if(line.startsWith("note ")){
				binding.notes.add(line.substring(5).strip());
			}
			else if(line.startsWith("class ")){
				String[] head = line.substring(6).split(" : ", 2);
				String[] kind = head[0].strip().split("\\s+");
				Cls c = cls(classes, resolve(kind[1], pkg), kind[0].equals("request"), api);
				addTokens(c, head[1], null);
			}
			else if(line.startsWith("M ")){
				binding = new Binding();
				String[] p = line.split("\\s+");
				String[] mm = p[1].split("/");
				binding.model = mm[0];
				binding.method = mm[1];
				binding.java = Character.toLowerCase(mm[1].charAt(0)) + mm[1].substring(1);
				binding.npModel = api.defaultModel;
				for(int i = 2; i < p.length; i++){
					if(p[i].startsWith("java=")) binding.java = p[i].substring(5);
					else if(p[i].startsWith("model=")) binding.npModel = p[i].substring(6);
					else if(p[i].startsWith("doc=")) binding.docs = p[i].substring(4);
				}
				api.bindings.add(binding);
			}
			else if(line.startsWith("req ") || line.startsWith("res ")){
				boolean request = line.startsWith("req ");
				Side side = request ? binding.req : binding.res;
				String body = line.substring(4).strip();
				if(body.equals("-")){
					continue;
				}
				side.none = false;
				String[] head = body.split(" : ", 2);
				boolean first = true;
				for(String ref : head[0].split(",")){
					ref = ref.strip();
					side.refs.add(ref);
					if(!ref.startsWith("=")){
						Cls c = cls(classes, resolve(ref, pkg), request, api);
						c.usedBy.add(binding.model + "/" + binding.method);
						if(first && head.length > 1){
							addTokens(c, head[1], binding);
						}
					}
					first = false;
				}
			}
			else{
				throw new IllegalStateException("Unrecognised spec line: " + line);
			}
		}

		int written = 0;
		for(Cls c : classes.values()){
			written += write(out, c.fqn, emitRecord(c, withExtra.contains(c.fqn))) ? 1 : 0;
		}
		for(Api a : apis){
			written += write(out, "com.novapost.client.api." + a.name, emitFacade(a, pkgOf(classes))) ? 1 : 0;
		}
		System.out.println("Generated/updated " + written + " files (" + classes.size() + " records, " + apis.size() + " facades).");
	}

	private static Map<String, String> pkgOf(Map<String, Cls> classes){
		Map<String, String> m = new LinkedHashMap<>();
		classes.keySet().forEach(f -> m.put(f.substring(f.lastIndexOf('.') + 1), f));
		return m;
	}

	private static String resolve(String ref, String pkg){
		if(ref.startsWith("=")){
			return ref.substring(1);
		}
		return ref.contains(".") ? ref : pkg + "." + ref;
	}

	private static Cls cls(Map<String, Cls> classes, String fqn, boolean request, Api api){
		Cls c = classes.computeIfAbsent(fqn, k -> new Cls(k, request));
		if(api != null){
			c.section = api.uuid;
		}
		return c;
	}

	private static void addTokens(Cls c, String tokens, Binding b){
		for(String t : tokens.strip().split("\\s+")){
			Matcher m = TOKEN.matcher(t);
			if(!m.matches()){
				throw new IllegalStateException("Bad token '" + t + "' in " + c.fqn);
			}
			String json = m.group(2);
			String java = m.group(3) != null ? m.group(3) : javaName(json);
			c.fields.putIfAbsent(json, new Tok(json, java, m.group(4), !m.group(1).isEmpty(), m.group(5)));
		}
	}

	static String javaName(String json){
		int n = json.length(), i = 0;
		while(i < n && Character.isUpperCase(json.charAt(i))){
			i++;
		}
		String r;
		if(i == 0) r = json;
		else if(i == 1) r = Character.toLowerCase(json.charAt(0)) + json.substring(1);
		else if(i == n) r = json.toLowerCase();
		else r = json.substring(0, i - 1).toLowerCase() + json.substring(i - 1);
		return KEYWORDS.contains(r) ? r + "Value" : r;
	}

	/** A type token may end in {@code !}: the request then sends the native JSON form (number) instead of the documented string form. */
	private static String baseType(Tok t){
		return t.type() == null ? null : t.type().endsWith("!") ? t.type().substring(0, t.type().length() - 1) : t.type();
	}

	private static boolean nativeWire(Tok t){
		return t.type() != null && t.type().endsWith("!");
	}

	private static String javaType(Tok t){
		String type = baseType(t);
		if(type == null) return "String";
		return switch(type){
			case "bool", "flag", "tbool" -> "Boolean";
			case "json" -> "JsonNode";
			case "strs" -> "List<String>";
			case "int" -> "Integer";
			case "long" -> "Long";
			case "dbl" -> "Double";
			case "dec" -> "BigDecimal";
			case "date" -> "LocalDate";
			case "datetime" -> "LocalDateTime";
			case "time" -> "LocalTime";
			default -> type;
		};
	}

	private static final String SUPPORT = "com.novapost.model.support.";

	/**
	 * Jackson annotations of a typed field. Answers are read tolerantly (any date format, "1"/"true", "12,5"...). Requests are written the way the
	 * documentation's examples show them: dates as {@code dd.MM.yyyy}, numbers and flags as strings ({@code "0.5"}, {@code "1"}).
	 * Token types: date, datetime, time, dec, int, long (add {@code !} for a native JSON number in requests), bool (a native JSON boolean),
	 * flag (request value {@code "1"}/{@code "0"}), tbool (request value {@code "true"}/{@code "false"}).
	 */
	private static String annotations(Tok t, boolean request, Set<String> imports){
		String type = baseType(t);
		if(type == null){
			return "";
		}
		StringBuilder a = new StringBuilder();
		switch(type){
			case "date" -> {
				if(request){
					imports.add("com.fasterxml.jackson.annotation.JsonFormat");
					a.append("@JsonFormat(pattern = \"dd.MM.yyyy\") ");
				}
				a.append(deserializer("NpDateDeserializer", imports));
			}
			case "datetime" -> {
				if(request){
					imports.add("com.fasterxml.jackson.annotation.JsonFormat");
					a.append("@JsonFormat(pattern = \"dd.MM.yyyy HH:mm:ss\") ");
				}
				a.append(deserializer("NpDateTimeDeserializer", imports));
			}
			case "time" -> {
				if(request){
					imports.add("com.fasterxml.jackson.annotation.JsonFormat");
					a.append("@JsonFormat(pattern = \"HH:mm\") ");
				}
				a.append(deserializer("NpTimeDeserializer", imports));
			}
			case "dec" -> {
				if(request && !nativeWire(t)){
					a.append(serializer("NpDecimalSerializer", imports));
				}
				a.append(deserializer("NpDecimalDeserializer", imports));
			}
			case "int", "long" -> {
				if(request && !nativeWire(t)){
					imports.add("com.fasterxml.jackson.annotation.JsonFormat");
					a.append("@JsonFormat(shape = JsonFormat.Shape.STRING) ");
				}
				if(type.equals("int")){
					a.append(deserializer("NpIntegerDeserializer", imports));
				}
			}
			case "bool" -> a.append(deserializer("NpBooleanDeserializer", imports));
			case "flag" -> {
				if(request){
					a.append(serializer("NpFlagSerializer", imports));
				}
				a.append(deserializer("NpBooleanDeserializer", imports));
			}
			case "tbool" -> {
				if(request){
					a.append(serializer("NpTextBooleanSerializer", imports));
				}
				a.append(deserializer("NpBooleanDeserializer", imports));
			}
			default -> {
				// json, strs, dbl and plain Java types need no annotation
			}
		}
		return a.toString();
	}

	private static String deserializer(String name, Set<String> imports){
		imports.add("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
		imports.add(SUPPORT + name);
		return "@JsonDeserialize(using = " + name + ".class) ";
	}

	private static String serializer(String name, Set<String> imports){
		imports.add("com.fasterxml.jackson.databind.annotation.JsonSerialize");
		imports.add(SUPPORT + name);
		return "@JsonSerialize(using = " + name + ".class) ";
	}

	private static void typeImports(String javaType, Set<String> imports){
		switch(javaType){
			case "BigDecimal" -> imports.add("java.math.BigDecimal");
			case "LocalDate" -> imports.add("java.time.LocalDate");
			case "LocalDateTime" -> imports.add("java.time.LocalDateTime");
			case "LocalTime" -> imports.add("java.time.LocalTime");
			default -> {
			}
		}
	}

	/** The Java source of a builder default ({@code ~value} in the spec). */
	private static String defaultLiteral(Tok t){
		String type = javaType(t);
		return switch(type){
			case "String" -> "\"" + t.dflt() + "\"";
			case "BigDecimal" -> "new BigDecimal(\"" + t.dflt() + "\")";
			default -> t.dflt();
		};
	}

	private static String emitRecord(Cls c, boolean extra){
		String simple = c.fqn.substring(c.fqn.lastIndexOf('.') + 1);
		String pkg = c.fqn.substring(0, c.fqn.lastIndexOf('.'));
		List<Tok> toks = new ArrayList<>(c.fields.values());
		Set<String> imports = new TreeSet<>();
		StringBuilder components = new StringBuilder();
		for(int i = 0; i < toks.size(); i++){
			Tok t = toks.get(i);
			components.append("\t\t@JsonProperty(\"").append(t.json()).append("\") ").append(annotations(t, c.request, imports));
			typeImports(javaType(t), imports);
			components.append(javaType(t)).append(' ').append(t.java()).append(i < toks.size() - 1 || extra ? ",\n" : "\n");
		}
		StringBuilder s = new StringBuilder();
		s.append("package ").append(pkg).append(";\n\n");
		s.append("import com.fasterxml.jackson.annotation.JsonAnyGetter;\n");
		s.append("import com.fasterxml.jackson.annotation.JsonAnySetter;\n");
		s.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n");
		s.append("import com.fasterxml.jackson.annotation.JsonInclude;\n");
		s.append("import com.fasterxml.jackson.annotation.JsonProperty;\n");
		s.append("import com.fasterxml.jackson.databind.JsonNode;\n");
		for(String i : imports){
			s.append("import ").append(i).append(";\n");
		}
		s.append("\nimport java.util.List;\n");
		s.append("import java.util.Map;\n\n");
		s.append("/**\n * ").append(c.request ? "Request" : "Response").append(" model for ").append(String.join(", ", c.usedBy.isEmpty() ? List.of("the Nova Post API") : c.usedBy.stream().map(x -> "{@code " + x + "}").toList()))
				.append(".\n * Docs: ").append(DOCS).append(c.section).append("\n * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;\n * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).\n */\n");
		s.append(c.request ? "@JsonInclude(JsonInclude.Include.NON_NULL)\n" : "@JsonIgnoreProperties(ignoreUnknown = true)\n");
		s.append("public record ").append(simple).append("(\n");
		s.append(components);
		if(extra){
			s.append("\t\t@JsonAnySetter Map<String, JsonNode> extra\n");
		}
		s.append("){\n");
		if(extra && !c.request){
			s.append("\n\t/** {@code extra} holds every field of the answer that this model does not declare (it is never null); the API returns more than the documentation lists. */\n");
			s.append("\tpublic ").append(simple).append("{\n\t\textra = extra == null ? Map.of() : Map.copyOf(extra);\n\t}\n\n");
			s.append("\t@JsonAnyGetter\n\tpublic Map<String, JsonNode> extra(){\n\t\treturn extra;\n\t}\n");
		}
		if(c.request){
			s.append("\n\tpublic static Builder builder(){\n\t\treturn new Builder();\n\t}\n\n");
			s.append("\tpublic static final class Builder{\n");
			for(Tok t : toks){
				s.append("\t\tprivate ").append(javaType(t)).append(' ').append(t.java());
				if(t.dflt() != null){
					s.append(" = ").append(defaultLiteral(t));
				}
				s.append(";\n");
			}
			s.append('\n');
			for(Tok t : toks){
				s.append("\t\tpublic Builder ").append(t.java()).append('(').append(javaType(t)).append(' ').append(t.java()).append("){ this.")
						.append(t.java()).append(" = ").append(t.java()).append("; return this; }\n");
			}
			s.append("\n\t\tpublic ").append(simple).append(" build(){\n\t\t\treturn new ").append(simple).append('(');
			for(int i = 0; i < toks.size(); i++){
				s.append(toks.get(i).java()).append(i < toks.size() - 1 ? ", " : "");
			}
			s.append(");\n\t\t}\n\t}\n");
		}
		s.append("}\n");
		return s.toString();
	}

	private static String emitFacade(Api a, Map<String, String> bySimple){
		Set<String> imports = new LinkedHashSet<>();
		imports.add("com.fasterxml.jackson.databind.JsonNode");
		imports.add("com.novapost.client.NovaPostClient");
		imports.add("com.novapost.client.NpCall");
		imports.add("com.novapost.client.NpModel");
		StringBuilder body = new StringBuilder();
		Set<String> emitted = new LinkedHashSet<>();
		for(Binding b : a.bindings){
			if(!emitted.add(b.java)){
				continue;
			}
			String reqType = typeOf(b.req, bySimple, imports);
			String resType = b.res.none ? "JsonNode" : typeOf(b.res, bySimple, imports);
			body.append("\n\t/**\n\t * {@code ").append(b.model).append('/').append(b.method).append("} (docs pages ").append(b.docs).append(").\n");
			for(String note : b.notes){
				body.append("\t * <p>").append(note.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("*/", "*&#47;")).append('\n');
			}
			body.append("\t */\n");
			body.append("\tpublic NpCall<").append(resType).append("> ").append(b.java).append('(')
					.append(reqType == null ? "" : reqType + " request").append("){\n");
			body.append("\t\treturn client.call(NpModel.").append(b.npModel).append(", \"").append(b.method).append("\", ")
					.append(reqType == null ? "null" : "request").append(", ").append(resType).append(".class);\n\t}\n");
		}
		StringBuilder s = new StringBuilder("package com.novapost.client.api;\n\n");
		for(String i : imports){
			s.append("import ").append(i).append(";\n");
		}
		s.append("\n/**\n * Facade for the \"").append(a.name.replace("Api", "")).append("\" section of the Nova Post API documentation\n * (").append(DOCS).append(a.uuid)
				.append(").\n * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.\n */\n");
		s.append("public final class ").append(a.name).append("{\n\n\tprivate final NovaPostClient client;\n\n\tpublic ").append(a.name)
				.append("(NovaPostClient client){\n\t\tthis.client = client;\n\t}\n").append(body).append("}\n");
		return s.toString();
	}

	private static String typeOf(Side side, Map<String, String> bySimple, Set<String> imports){
		if(side.none){
			return null;
		}
		String ref = side.refs.get(0);
		String fqn = ref.startsWith("=") ? ref.substring(1) : ref.contains(".") ? ref : bySimple.get(ref);
		if(fqn == null){
			throw new IllegalStateException("Unknown class " + ref);
		}
		imports.add(fqn);
		return fqn.substring(fqn.lastIndexOf('.') + 1);
	}

	private static boolean write(Path root, String fqn, String content) throws IOException{
		Path file = root.resolve(fqn.replace('.', '/') + ".java");
		if(Files.exists(file) && Files.readString(file).contains("// @keep") && !fqn.startsWith("com.novapost.client.api.")){
			return false;
		}
		Files.createDirectories(file.getParent());
		Files.writeString(file, content);
		return true;
	}
}
