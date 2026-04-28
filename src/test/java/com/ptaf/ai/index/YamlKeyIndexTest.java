package com.ptaf.ai.index;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlKeyIndexTest {

    @TempDir
    Path tempDir;

    @Test
    void flattensNestedYamlKeysAndScansYmlAndYaml() throws Exception {
        Path elements = tempDir.resolve("src/test/resources/elements");
        Path api = tempDir.resolve("src/test/resources/api_requests");
        Files.createDirectories(elements);
        Files.createDirectories(api);

        Files.writeString(elements.resolve("login.yml"), """
                login:
                  username:
                    input: "#username"
                """, StandardCharsets.UTF_8);
        Files.writeString(api.resolve("users.yaml"), """
                users:
                  createUser:
                    method: "POST"
                """, StandardCharsets.UTF_8);

        Map<String, String> paths = new LinkedHashMap<>();
        paths.put("elements", "src/test/resources/elements");
        paths.put("api_requests", "src/test/resources/api_requests");

        YamlKeyIndex index = YamlKeyIndex.build(tempDir, paths);
        assertTrue(index.normalizedKeys().contains("elements.login"));
        assertTrue(index.normalizedKeys().contains("elements.login.username"));
        assertTrue(index.normalizedKeys().contains("elements.login.username.input"));
        assertTrue(index.normalizedKeys().contains("api_requests.users.createuser.method"));
    }
}
