package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationPropertiesTest {

    @Value("${ollama.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    @Value("${ollama.timeout-seconds}")
    private int ollamaTimeoutSeconds;

    @Value("${query.default-max-sources}")
    private int queryDefaultMaxSources;

    @Test
    void propertiesAreLoadedCorrectly() {
        // Verify runtime configuration values are loaded from application.properties
        assertThat(ollamaUrl).isEqualTo("http://localhost:11434");
        assertThat(ollamaModel).isEqualTo("llama3.2:1b");
        assertThat(ollamaTimeoutSeconds).isEqualTo(10);
        assertThat(queryDefaultMaxSources).isEqualTo(10);
    }
}
