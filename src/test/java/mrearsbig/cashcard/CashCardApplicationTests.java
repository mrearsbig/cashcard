package mrearsbig.cashcard;

import java.net.URI;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
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
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		// Check if the response status is OK
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		Number id = documentContext.read("$.id", Number.class);
		Assertions.assertThat(id).isEqualTo(99);

		Double amount = documentContext.read("$.amount", Double.class);
		Assertions.assertThat(amount).isEqualTo(123.45);
	}

	@Test
	/**
	 * This test checks that the application does not return a cash card
	 * when an unknown ID is requested.
	 */
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/1000", String.class);

		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		Assertions.assertThat(response.getBody()).isBlank();
	}

	@Test
	/**
	 * This test checks that the application creates a new cash card
	 * when the appropriate endpoint is called.
	 */
	@DirtiesContext
	void shouldCreateANewCashCard() {
		CashCard newCashCard = new CashCard(null, 200.00, null);
		ResponseEntity<Void> createResponse = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);
		Assertions.assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
		ResponseEntity<String> getResponse = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);
		Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

		Number id = documentContext.read("$.id", Number.class);
		Assertions.assertThat(id).isNotNull();

		Double amount = documentContext.read("$.amount", Double.class);
		Assertions.assertThat(amount).isEqualTo(200.00);
	}

	@Test
	/**
	 * This test checks that the application returns all cash cards
	 * when the list endpoint is called.
	 */
	void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		int cashCardCount = documentContext.read("$.length()", Integer.class);
		Assertions.assertThat(cashCardCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id", JSONArray.class);
		Assertions.assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		JSONArray amounts = documentContext.read("$..amount", JSONArray.class);
		Assertions.assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.00, 150.00);
	}

	@Test
	/**
	 * This test checks that the application returns a paginated list of cash cards
	 * when the appropriate endpoint is called.
	 */
	void shouldReturnAPageOfCashCards() {
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]", JSONArray.class);
		Assertions.assertThat(page.size()).isEqualTo(1);
	}

	@Test
	/**
	 * This test checks that the application returns a sorted page of cash cards
	 * when the appropriate endpoint is called with sorting parameters.
	 */
	void shouldReturnASortedPageOfCashCards() {
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]", JSONArray.class);
		Assertions.assertThat(page.size()).isEqualTo(1);

		Double amount = documentContext.read("$[0].amount", Double.class);
		Assertions.assertThat(amount).isEqualTo(150.00);
	}

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = testRestTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]", JSONArray.class);
		Assertions.assertThat(page.size()).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount", JSONArray.class);
		Assertions.assertThat(amounts).containsExactly(1.00, 123.45, 150.00);
	}

	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		ResponseEntity<String> response = testRestTemplate
		.withBasicAuth("BAD-USER", "abc123")
		.getForEntity("/cashcards/99", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		response = testRestTemplate
		.withBasicAuth("sarah1", "BAD-PASSWORD")
		.getForEntity("/cashcards/99", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = testRestTemplate
		.withBasicAuth("hank-owns-no-cards", "qrs456")
		.getForEntity("/cashcards/99", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = testRestTemplate
		.withBasicAuth("sarah1", "abc123")
		.getForEntity("/cashcards/102", String.class);
		Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
