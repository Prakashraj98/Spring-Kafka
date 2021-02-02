package com.learnkafka.integration.test.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"},partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
		"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class LibraryEventControllerTest {

	@Autowired
	TestRestTemplate testRestTemplate;

	private Consumer<Integer,String> consumer;
	
	@Autowired
	EmbeddedKafkaBroker embeddedKafkaBroker;
	
	@BeforeEach
	void setUp() {
		
		Map<String,Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
		consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new org.apache.kafka.common.serialization.StringDeserializer()).createConsumer();
		embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
	}
	
	@AfterEach
	void tearDown() {
		consumer.close();
	}
	
	@Test
	void postLibraryEvent() {

		Book book = Book.builder().bookId(101).bookAuthor("chetan").bookName("half gf").build();

		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();

		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", MediaType.APPLICATION_JSON.toString());
		HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

		ResponseEntity<LibraryEvent> response = testRestTemplate.exchange("/v1/libraryevent", HttpMethod.POST, request,
				LibraryEvent.class);
		
		assertEquals(HttpStatus.CREATED,response.getStatusCode());
		
		ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "library-events");
		String actual = consumerRecord.value();
		String expected = "{\"libraryEventId\":null,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":101,\"bookName\":\"half gf\",\"bookAuthor\":\"chetan\"}}";
		
		assertEquals(expected, actual);
	}

}
