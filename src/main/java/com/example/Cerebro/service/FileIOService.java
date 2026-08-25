package com.example.Cerebro.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.example.Cerebro.record.GeneratedArtifact;

@Service
public class FileIOService {
    public Path createWorkspace() {
        try {
            return Files.createTempDirectory("devops-session-");
        } catch (IOException e) {
            throw new RuntimeException("Failed to create workspace", e);
        }
    }

    public void writeArtifact(Path workspace, GeneratedArtifact artifact) {
        try {
            Path filePath = workspace.resolve(artifact.filename());
            Files.writeString(filePath, artifact.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write artifact to disk", e);
        }
    }

    public void cleanup(Path workspace) {
        try {
            if (Files.exists(workspace)) {
                Files.walk(workspace)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("Could not delete: " + path);
                            }
                        });
            }
        } catch (IOException e) {
            System.err.println("Cleanup failed for workspace: " + workspace);
        }
    }
}
