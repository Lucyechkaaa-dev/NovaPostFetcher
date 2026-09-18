package com.novapost;

import com.novapost.controller.NovaPostController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NovaPostControllerTest{

	@Test
	void testControllerInitialization(){
		NovaPostController controller = NovaPostController.create("dummy-api-key");
		assertNotNull(controller);
		assertNotNull(controller.getClient());
		assertEquals("dummy-api-key", controller.getClient().getApiKey());
	}

	@Test
	void testControllerRejectsNullClient(){
		assertThrows(IllegalArgumentException.class, () -> new NovaPostController(null));
	}
}
