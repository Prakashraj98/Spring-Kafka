package com.learnkafka.unit.test.controller;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;

@ExtendWith(MockitoExtension.class)
class LibraryEventProducerUnitTest {

	@Mock
	KafkaTemplate<Integer, String> kafkaTemplate;

	@Spy
	ObjectMapper objectMapper;

	@InjectMocks
	LibraryEventProducer libraryEventProducer;
	
	@Test
	void test() throws JsonProcessingException {

		Book book = Book.builder().bookId(101).bookAuthor("chetan").bookName("half gf").build();

		LibraryEvent libraryEvent = LibraryEvent.builder().libraryEventId(null).book(null).build();
		
		Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        
        ProducerRecord<Integer, String>  producerRecord = buildProducerRecord(key,value,"library-events");
        
        SettableListenableFuture future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("Error in test failure"));
        
        when(kafkaTemplate.send(producerRecord)).thenReturn(future);
		assertThrows(Exception.class,()->libraryEventProducer.sendLibraryEventApproach2(libraryEvent).get());
	}
	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
    	List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
		return new ProducerRecord<Integer, String>(topic,null, key, value,recordHeaders);
	}
}
