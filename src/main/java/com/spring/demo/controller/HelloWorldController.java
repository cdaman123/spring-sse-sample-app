package com.spring.demo.controller;

import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@RestController
public class HelloWorldController {

	@GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter proxySse1() {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

		emitter.onTimeout(() -> System.out.println("SSE connection timed out"));
		emitter.onCompletion(() -> System.out.println("SSE connection closed"));

		new Thread(() -> {
			int count = 0;
			while (true) {
				count++;
				try {

					String data = "Event at: " + LocalDateTime.now() + " Count: " + Integer.toString(count);
					SseEventBuilder eventBuilder = SseEmitter.event().data(data);
					emitter.send(eventBuilder);
					Thread.sleep(1000); // Send event every 100ms (adjust as needed)
				} catch (Exception e) {
					emitter.completeWithError(e);
					break;
				}
				if (count == 20) { // Number of events to be send (adjust as needed)
					emitter.complete();
					break;
				}
			}
		}).start();

		return emitter;
	}

}
