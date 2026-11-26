package com.novapost;

import com.novapost.client.Pager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PagerTest{

	@Test
	void loadsUntilAShortPage(){
		List<Integer> requested = new ArrayList<>();

		List<String> all = Pager.fetchAll(3, page -> {
			requested.add(page);
			return switch(page){
				case 1 -> List.of("a", "b", "c");
				case 2 -> List.of("d", "e", "f");
				default -> List.of("g");
			};
		});

		assertEquals(List.of("a", "b", "c", "d", "e", "f", "g"), all);
		assertEquals(List.of(1, 2, 3), requested);
	}

	@Test
	void stopsOnAnEmptyPage(){
		assertEquals(List.of("a", "b"), Pager.fetchAll(2, page -> page == 1 ? List.of("a", "b") : List.of()));
		assertTrue(Pager.fetchAll(2, page -> List.<String>of()).isEmpty());
	}

	@Test
	void stopsWhenTheServerIgnoresPage(){
		List<Integer> requested = new ArrayList<>();

		List<String> all = Pager.fetchAll(2, page -> {
			requested.add(page);
			return List.of("same", "page");
		});

		assertEquals(List.of("same", "page"), all);
		assertEquals(List.of(1, 2), requested);
	}

	@Test
	void hardStopOnMaxPages(){
		int[] calls = {0};

		List<Integer> all = Pager.fetchAll(1, 5, page -> {
			calls[0]++;
			return List.of(page);
		});

		assertEquals(5, calls[0]);
		assertEquals(List.of(1, 2, 3, 4, 5), all);
	}

	@Test
	void documentedPageSizes(){
		assertEquals(500, Pager.WAREHOUSES);
		assertEquals(500, Pager.STREETS);
		assertEquals(100, Pager.COUNTERPARTIES);
		assertEquals(100, Pager.DOCUMENTS);
		assertThrows(IllegalArgumentException.class, () -> Pager.fetchAll(0, page -> List.of()));
	}
}
