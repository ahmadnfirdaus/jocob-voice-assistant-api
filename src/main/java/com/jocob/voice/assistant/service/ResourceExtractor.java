package com.jocob.voice.assistant.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;

@Service
public class ResourceExtractor {

    public File extractResourceDir(String resourcePath) throws IOException, URISyntaxException {
        Path tempDir = Files.createTempDirectory("vosk-model-");
        URL resourceURL = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
        System.out.println("Resource URL: " + resourceURL.getProtocol() + " : " + resourceURL);

        if (resourceURL == null) {
            throw new FileNotFoundException("Resource folder not found: " + resourcePath);
        }

        if ("jar".equals(resourceURL.getProtocol()) || resourceURL.toString().startsWith("nested:")) {
            System.out.println("Incoming JAR");

            String jarPath = resourceURL.toString();
            jarPath = jarPath.substring(jarPath.indexOf(":") + 1, jarPath.indexOf("!BOOT-INF"));
            jarPath = jarPath.replace("nested:", "").replace("file:", "");

            try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
                jarFile.stream()
                        .filter(e -> e.getName().startsWith("BOOT-INF/classes/" + resourcePath + "/"))
                        .forEach(entry -> {
                            try {
                                String relativeName = entry.getName()
                                        .substring(("BOOT-INF/classes/" + resourcePath + "/").length());
                                Path dest = tempDir.resolve(relativeName);
                                if (entry.isDirectory()) {
                                    Files.createDirectories(dest);
                                } else {
                                    Files.createDirectories(dest.getParent());
                                    try (InputStream in = jarFile.getInputStream(entry)) {
                                        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
                                    }
                                }
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                        });
            }
        } else {
            System.out.println("Incoming Local");

            Path sourcePath = Paths.get(resourceURL.toURI());
            Files.walk(sourcePath).forEach(path -> {
                try {
                    Path dest = tempDir.resolve(sourcePath.relativize(path).toString());
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(path, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            });
        }

        return tempDir.toFile();
    }
}
