package com.novapost.model;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A request to the print service ({@code my.novaposhta.ua}), built in the URL forms the documentation describes
 * (section "Друк", {@code PrintGeneral}).
 * <ul>
 *   <li>marking, A4 sheet with 6 labels: {@code printMarking85x85} with {@code type/pdf8} (or {@code html});</li>
 *   <li>marking for a Zebra printer: {@code printMarking100x100} with {@code type/pdf} (or {@code html}) and the trailing {@code zebra} flag;</li>
 *   <li>express waybill: {@code printDocument} with {@code type/pdf} or {@code html};</li>
 *   <li>scan sheet: {@code refs[]/{id}}, exactly one id per request.</li>
 * </ul>
 * Several documents are joined with a comma ({@code orders/n1,n2}); more than 100 markings need {@code limit}/{@code page}.
 * The documented {@code orders[]/n1/orders[]/n2} form is available through {@link Builder#arrayStyle(boolean)}.
 */
public final class PrintRequest{

	private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9-]+");
	private static final String BASE = "https://my.novaposhta.ua/";

	public enum Doc{
		MARKING_85X85("orders", "printMarking85x85"),
		MARKING_100X100("orders", "printMarking100x100"),
		/** Not in the current documentation; kept for callers that rely on it. */
		MARKINGS_LEGACY("orders", "printMarkings"),
		DOCUMENT("orders", "printDocument"),
		SCAN_SHEET("scanSheet", "printScanSheet");

		private final String module;
		private final String method;

		Doc(String module, String method){
			this.module = module;
			this.method = method;
		}

		public String module(){
			return module;
		}

		public String method(){
			return method;
		}
	}

	public enum Type{
		PDF("pdf"),
		/** A4 sheet with several labels ({@code printMarking85x85}). */
		PDF8("pdf8"),
		HTML("html");

		private final String value;

		Type(String value){
			this.value = value;
		}

		public String value(){
			return value;
		}

		public boolean isPdf(){
			return this != HTML;
		}
	}

	private final Doc doc;
	private final Type type;
	private final boolean zebra;
	private final boolean arrayStyle;
	private final List<String> ids;
	private final Integer limit;
	private final Integer page;

	private PrintRequest(Builder b){
		this.doc = Objects.requireNonNull(b.doc, "doc must not be null");
		this.type = b.type != null ? b.type : Type.PDF;
		this.zebra = b.zebra;
		this.arrayStyle = b.arrayStyle;
		this.limit = b.limit;
		this.page = b.page;
		if(b.ids == null || b.ids.isEmpty()){
			throw new IllegalArgumentException("Document reference list must not be empty");
		}
		this.ids = b.ids.stream().map(String::trim).toList();
		for(String id : ids){
			if(!SAFE_ID.matcher(id).matches()){
				throw new IllegalArgumentException("Illegal document reference '" + id + "'");
			}
		}
		if(doc == Doc.SCAN_SHEET && ids.size() != 1){
			throw new IllegalArgumentException("A scan sheet is printed one at a time: pass exactly one Ref or number");
		}
	}

	public static Builder builder(){
		return new Builder();
	}

	public Doc doc(){
		return doc;
	}

	public Type type(){
		return type;
	}

	public boolean zebra(){
		return zebra;
	}

	public List<String> ids(){
		return ids;
	}

	/** The URL; it contains the API key, so do not log it. */
	public String toUrl(String apiKey){
		if(apiKey == null || apiKey.isBlank()){
			throw new IllegalArgumentException("API key must not be null or blank");
		}
		StringBuilder sb = new StringBuilder(BASE);
		if(doc == Doc.SCAN_SHEET){
			sb.append("scanSheet/printScanSheet/refs[]/").append(ids.getFirst());
		}
		else{
			sb.append("orders/").append(doc.method()).append('/');
			if(arrayStyle){
				sb.append(String.join("/", ids.stream().map(id -> "orders[]/" + id).toList()));
			}
			else{
				sb.append("orders/").append(String.join(",", ids));
			}
		}
		sb.append("/type/").append(type.value()).append("/apiKey/").append(apiKey.trim());
		if(limit != null){
			sb.append("/limit/").append(limit);
		}
		if(page != null){
			sb.append("/page/").append(page);
		}
		if(zebra){
			sb.append("/zebra");
		}
		return sb.toString();
	}

	@Override
	public String toString(){
		return "PrintRequest[" + doc + ", " + type + (zebra ? ", zebra" : "") + ", ids=" + ids + "]";
	}

	public static final class Builder{

		private Doc doc;
		private Type type;
		private boolean zebra;
		private boolean arrayStyle;
		private List<String> ids;
		private Integer limit;
		private Integer page;

		public Builder doc(Doc doc){
			this.doc = doc;
			return this;
		}

		public Builder type(Type type){
			this.type = type;
			return this;
		}

		public Builder zebra(boolean zebra){
			this.zebra = zebra;
			return this;
		}

		/** Use {@code orders[]/n1/orders[]/n2} instead of {@code orders/n1,n2}. */
		public Builder arrayStyle(boolean arrayStyle){
			this.arrayStyle = arrayStyle;
			return this;
		}

		public Builder ids(List<String> ids){
			this.ids = ids;
			return this;
		}

		public Builder id(String id){
			this.ids = List.of(id);
			return this;
		}

		/** For more than 100 markings in one request (the documented default maximum is 100). */
		public Builder limit(Integer limit){
			this.limit = limit;
			return this;
		}

		public Builder page(Integer page){
			this.page = page;
			return this;
		}

		public PrintRequest build(){
			return new PrintRequest(this);
		}
	}
}
