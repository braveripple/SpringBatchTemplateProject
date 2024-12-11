package com.example.springbatch5template.component;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("development")
class PGCopyClientTest {

	@Autowired
	private PGCopyClient pgCopyClient;
	
	private final Path tempfile = Paths.get("tempfile.csv");
	
	@Test
	void test() {


	}

}