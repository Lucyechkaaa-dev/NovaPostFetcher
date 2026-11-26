package com.novapost.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Loads every page of a paged method. It stops on an empty or short page and also when a page is identical to the previous one,
 * which is what a server that ignores {@code Page} would otherwise make an endless loop out of.
 *
 * <pre>
 * List&lt;Warehouse&gt; all = Pager.fetchAll(Pager.WAREHOUSES,
 *         page -&gt; client.address().getWarehouses(WarehouseFilter.byCityRef(cityRef, page, Pager.WAREHOUSES)).list());
 * </pre>
 */
public final class Pager{

	/** Documented maximum page sizes. */
	public static final int WAREHOUSES = 500;
	public static final int STREETS = 500;
	public static final int CARGO_DESCRIPTIONS = 500;
	public static final int COUNTERPARTIES = 100;
	public static final int CONTACT_PERSONS = 100;
	/** {@code getDocumentList} in paged mode ({@code GetFullList=0}). */
	public static final int DOCUMENTS = 100;

	/** No documented method has anywhere near this many pages. */
	public static final int DEFAULT_MAX_PAGES = 10_000;

	private Pager(){
	}

	public static <T> List<T> fetchAll(int pageSize, IntFunction<List<T>> fetchPage){
		return fetchAll(pageSize, DEFAULT_MAX_PAGES, fetchPage);
	}

	/**
	 * @param pageSize a page with fewer items than this is the last one
	 * @param maxPages hard stop
	 * @param fetchPage returns the items of the given page, numbered from 1
	 */
	public static <T> List<T> fetchAll(int pageSize, int maxPages, IntFunction<List<T>> fetchPage){
		if(pageSize < 1){
			throw new IllegalArgumentException("pageSize must be positive");
		}
		List<T> all = new ArrayList<>();
		List<T> previous = null;
		for(int page = 1; page <= maxPages; page++){
			List<T> items = fetchPage.apply(page);
			if(items == null || items.isEmpty()){
				break;
			}
			if(items.equals(previous)){
				break;
			}
			all.addAll(items);
			if(items.size() < pageSize){
				break;
			}
			previous = items;
		}
		return all;
	}
}
