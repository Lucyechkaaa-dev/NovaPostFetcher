package com.novapost.testsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads {@code novaposhta/api-spec.txt} (the documentation snapshot) for the tests; see {@code tools/ModelGenerator.java} for the syntax. */
public final class ApiSpec{

	/** One facade method: the documented model/method, the page ids, and the Java classes of its request and answer. */
	public record Binding(String api, String model, String method, String java, String npModel, List<String> docs,
	                      List<Class<?>> reqClasses, List<Class<?>> resClasses, List<String> reqNames, List<String> resNames,
	                      List<String> reqExtras, List<String> resExtras, boolean hasReq,
	                      java.util.Map<String, String> reqTypes, java.util.Map<String, String> resTypes){

	}

	private static final Pattern TOKEN = Pattern.compile("(\\+?)([A-Za-z][A-Za-z0-9]*)(?:=([A-Za-z][A-Za-z0-9]*))?(?::([^~]+))?(?:~(.+))?");
	private static final List<Binding> BINDINGS = load();

	private ApiSpec(){
	}

	public static List<Binding> bindings(){
		return BINDINGS;
	}

	/** The binding whose documentation page has this id (first 8 characters). */
	public static Optional<Binding> byPage(String pageId){
		return BINDINGS.stream().filter(b -> b.docs().contains(pageId)).findFirst();
	}

	private static Class<?> load(String fqn){
		try{
			return Class.forName(fqn);
		}
		catch(ClassNotFoundException e){
			throw new IllegalStateException("Class from the spec does not exist: " + fqn, e);
		}
	}

	private static List<Binding> load(){
		try(InputStream in = ApiSpec.class.getResourceAsStream("/novaposhta/api-spec.txt")){
			if(in == null){
				throw new IllegalStateException("api-spec.txt is missing from the test resources");
			}
			List<String> lines = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines().toList();
			List<Binding> out = new ArrayList<>();
			String pkg = "com.novapost.model";
			String api = null, defaultModel = null;
			String model = null, method = null, java = null, npModel = null, docs = null;
			List<Class<?>> reqC = new ArrayList<>(), resC = new ArrayList<>();
			List<String> reqN = new ArrayList<>(), resN = new ArrayList<>(), reqX = new ArrayList<>(), resX = new ArrayList<>();
			boolean hasReq = false, open = false;
			java.util.Map<String, String> reqT = new java.util.LinkedHashMap<>(), resT = new java.util.LinkedHashMap<>();
			for(String raw : lines){
				String line = raw.strip();
				if(line.isEmpty() || line.startsWith("#")){
					continue;
				}
				boolean boundary = line.startsWith("M ") || line.startsWith("@") || line.startsWith("class ");
				if(boundary && open){
					out.add(new Binding(api, model, method, java, npModel, List.of(docs.split(",")), reqC, resC, reqN, resN, reqX, resX, hasReq, reqT, resT));
					open = false;
				}
				if(line.startsWith("@pkg ")){
					pkg = line.substring(5).strip();
				}
				else if(line.startsWith("@api ")){
					String[] p = line.split("\\s+");
					api = p[1];
					defaultModel = p[3];
				}
				else if(line.startsWith("M ")){
					String[] p = line.split("\\s+");
					String[] mm = p[1].split("/");
					model = mm[0];
					method = mm[1];
					java = Character.toLowerCase(mm[1].charAt(0)) + mm[1].substring(1);
					npModel = defaultModel;
					for(int i = 2; i < p.length; i++){
						if(p[i].startsWith("java=")) java = p[i].substring(5);
						else if(p[i].startsWith("model=")) npModel = p[i].substring(6);
						else if(p[i].startsWith("doc=")) docs = p[i].substring(4);
					}
					reqC = new ArrayList<>();
					resC = new ArrayList<>();
					reqN = new ArrayList<>();
					resN = new ArrayList<>();
					reqX = new ArrayList<>();
					resX = new ArrayList<>();
					hasReq = false;
					reqT = new java.util.LinkedHashMap<>();
					resT = new java.util.LinkedHashMap<>();
					open = true;
				}
				else if(line.startsWith("req ") || line.startsWith("res ")){
					boolean request = line.startsWith("req ");
					String body = line.substring(4).strip();
					if(body.equals("-")){
						continue;
					}
					if(request){
						hasReq = true;
					}
					String[] head = body.split(" : ", 2);
					for(String ref : head[0].split(",")){
						String fqn = ref.strip();
						fqn = fqn.startsWith("=") ? fqn.substring(1) : fqn.contains(".") ? fqn : pkg + "." + fqn;
						(request ? reqC : resC).add(load(fqn));
					}
					if(head.length > 1){
						for(String t : head[1].strip().split("\\s+")){
							Matcher m = TOKEN.matcher(t);
							if(!m.matches()){
								throw new IllegalStateException("Bad token " + t);
							}
							(m.group(1).isEmpty() ? (request ? reqN : resN) : (request ? reqX : resX)).add(m.group(2));
							if(m.group(4) != null){
								(request ? reqT : resT).put(m.group(2), m.group(4));
							}
						}
					}
				}
				// "note", "extra" and "class" lines carry no information the tests need
			}
			if(open){
				out.add(new Binding(api, model, method, java, npModel, List.of(docs.split(",")), reqC, resC, reqN, resN, reqX, resX, hasReq, reqT, resT));
			}
			return List.copyOf(out);
		}
		catch(IOException e){
			throw new IllegalStateException(e);
		}
	}
}
