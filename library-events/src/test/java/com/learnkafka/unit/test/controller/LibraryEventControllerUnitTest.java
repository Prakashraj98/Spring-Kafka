package com.learnkafka.unit.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.controller.LibraryEventsController;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
class LibraryEventControllerUnitTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@MockBean
	LibraryEventProducer libraryEventProducer;
	
	@Test
	void postLibraryEvent() throws Exception {
		
		Book book = Book.builder().bookId(101).bookAuthor("chetan").bookName("half gf").build();

		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(book).build();
		
		String json = objectMapper.writeValueAsString(libraryEvent);
		doNothing().when(libraryEventProducer).sendLibraryEventApproach2(libraryEvent);
		
		mockMvc.perform(post("/v1/libraryevent")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isCreated());
		
		
	}
	
}
