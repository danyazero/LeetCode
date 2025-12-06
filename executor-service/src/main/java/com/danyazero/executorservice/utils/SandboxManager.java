package com.danyazero.executorservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SandboxManager {

    public static Path createSandbox() {
        try {
            Path sandbox = Files.createTempDirectory("sbx_");
            log.debug("Created sandbox: {}", sandbox);
            return sandbox;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create sandbox", ex);
        }
    }

    public static void cleanup(Path sandbox) {
        if (sandbox == null) return;

        try {
            try (var walk = Files.walk(sandbox)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete: {}", path, e);
                            }
                        });
            }

            log.debug("Cleaned sandbox {}", sandbox);

        } catch (IOException ex) {
            log.error("Failed to clean sandbox {}", sandbox, ex);
        }
    }

    public static Path writeFile(Path sandbox, String filename, String content) {
        try {
            Path file = sandbox.resolve(filename);
            Files.writeString(file, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return file;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write sandbox file " + filename, ex);
        }
    }

    public static Map<String, Path> writeFiles(Path sandbox, Map<String, String> files) {
        Map<String, Path> result = new HashMap<>();
        files.forEach((name, content) -> result.put(name, writeFile(sandbox, name, content)));
        return result;
    }

    public static void ensureExists(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to ensure directory: " + dir, ex);
        }
    }

}
