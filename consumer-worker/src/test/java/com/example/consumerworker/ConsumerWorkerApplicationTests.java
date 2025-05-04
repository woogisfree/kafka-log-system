package com.example.consumerworker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@EmbeddedKafka(partitions = 1, topics = "user-activity-log")
@ActiveProfiles("test")
@SpringBootTest
class ConsumerWorkerApplicationTests {

	@Test
	void contextLoads() {
	}

}
