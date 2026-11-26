package com.novapost.testsupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.function.Function;

/** A fake {@link HttpClient} that records every JSON request body and answers with whatever the responder returns. */
public final class RecordingHttpClient extends HttpClient{

	public static final String EMPTY_OK = "{\"success\":true,\"data\":[],\"errors\":[],\"warnings\":[],\"info\":[],\"messageCodes\":[],\"errorCodes\":[]}";
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private final Function<String, String> responder;
	private final List<String> bodies = new ArrayList<>();
	private final List<URI> uris = new ArrayList<>();

	public RecordingHttpClient(Function<String, String> responder){
		this.responder = responder;
	}

	/** Always answers with an empty successful envelope. */
	public static RecordingHttpClient ok(){
		return new RecordingHttpClient(body -> EMPTY_OK);
	}

	/** Always answers with the same body. */
	public static RecordingHttpClient always(String body){
		return new RecordingHttpClient(request -> body);
	}

	public static String envelope(String dataJson){
		return "{\"success\":true,\"data\":" + dataJson + ",\"errors\":[],\"warnings\":[],\"info\":[],\"messageCodes\":[],\"errorCodes\":[]}";
	}

	public List<String> bodies(){
		return bodies;
	}

	public List<URI> uris(){
		return uris;
	}

	public int calls(){
		return bodies.size();
	}

	public JsonNode request(int index){
		try{
			return MAPPER.readTree(bodies.get(index));
		}
		catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	public JsonNode lastRequest(){
		return request(bodies.size() - 1);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler){
		String body = "";
		if(request.bodyPublisher().isPresent()){
			StringBuilder sb = new StringBuilder();
			request.bodyPublisher().get().subscribe(new Flow.Subscriber<>(){
				@Override public void onSubscribe(Flow.Subscription s){ s.request(Long.MAX_VALUE); }
				@Override public void onNext(ByteBuffer item){ sb.append(StandardCharsets.UTF_8.decode(item)); }
				@Override public void onError(Throwable t){ }
				@Override public void onComplete(){ }
			});
			body = sb.toString();
		}
		bodies.add(body);
		uris.add(request.uri());
		return (HttpResponse<T>) new Response(responder.apply(body), request);
	}

	@Override
	public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler){
		return CompletableFuture.completedFuture(send(request, handler));
	}

	@Override
	public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler,
	                                                        HttpResponse.PushPromiseHandler<T> pushPromiseHandler){
		return CompletableFuture.completedFuture(send(request, handler));
	}

	@Override public Optional<CookieHandler> cookieHandler(){ return Optional.empty(); }
	@Override public Optional<Duration> connectTimeout(){ return Optional.empty(); }
	@Override public Redirect followRedirects(){ return Redirect.NORMAL; }
	@Override public Optional<ProxySelector> proxy(){ return Optional.empty(); }
	@Override public SSLContext sslContext(){ return null; }
	@Override public SSLParameters sslParameters(){ return null; }
	@Override public Optional<Authenticator> authenticator(){ return Optional.empty(); }
	@Override public Version version(){ return Version.HTTP_2; }
	@Override public Optional<Executor> executor(){ return Optional.empty(); }

	private record Response(String body, HttpRequest request) implements HttpResponse<String>{

		@Override public int statusCode(){ return 200; }
		@Override public Optional<HttpResponse<String>> previousResponse(){ return Optional.empty(); }
		@Override public HttpHeaders headers(){ return HttpHeaders.of(Map.of(), (k, v) -> true); }
		@Override public Optional<SSLSession> sslSession(){ return Optional.empty(); }
		@Override public URI uri(){ return request.uri(); }
		@Override public HttpClient.Version version(){ return HttpClient.Version.HTTP_2; }
	}
}
