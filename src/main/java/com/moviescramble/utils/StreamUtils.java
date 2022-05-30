package com.moviescramble.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class StreamUtils {
    public static void parseCSV(String filename, Consumer<String> c) throws IOException {
        File file = new ClassPathResource(filename).getFile();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            lines.skip(1).forEach(c);
        }
    }
}
