package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {

	@Autowired
	LibraryEventProducer libraryEventProducer;
	
    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

    	log.info("Before sending message");
       // libraryEventProducer.sendLibraryEvent(libraryEvent);
    	//SendResult<Integer,String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
    	libraryEvent.setLibraryEventType(LibraryEventType.NEW);
    	libraryEventProducer.sendLibraryEventApproach2(libraryEvent);
    	//log.info("Producer Record is {} and Metadata is {}",sendResult.getProducerRecord(),sendResult.getRecordMetadata());
    	log.info("After sending message");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
