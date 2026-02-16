package com.intuit.challange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ChallangeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName ("Main - Verifica que el método main inicie la aplicación")
	void main() {
		// Ejecutamos el main y asertamos que no lance ninguna excepción
		assertDoesNotThrow(() -> ChallangeApplication.main(new String[] {}));
	}

}
