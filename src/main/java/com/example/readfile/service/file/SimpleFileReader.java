package com.example.readfile.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;


public class SimpleFileReader implements FileReader {
    @Override
    public Stream<String> read(Path path) throws IOException {
        return Files.lines(path);
    }
}
