package mrearsbig.cashcard;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashCardApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	/**
	 * This test checks that the application returns a cash card
	 * when the data is saved in the database.
	 */
	void shouldReturnACashCardWhenDataIsSaved() {
		ResponseEntity<String> response = testRestTemplate.getForEntity("/cashcard/1", String.class);
		// Check if the response status is OK
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id", Number.class);
		Assertions.assertThat(id).isEqualTo(1);

		Double amount = documentContext.read("$.amount", Double.class);
		Assertions.assertThat(amount).isEqualTo(123.45);
	}

	@Test
	/**
	 * This test checks that the application does not return a cash card
	 * when an unknown ID is requested.
	 */
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = testRestTemplate.getForEntity("/cashcard/2", String.class);

		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		Assertions.assertThat(response.getBody()).isBlank();
	}

}
