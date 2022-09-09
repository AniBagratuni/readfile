package com.example.readfile.service.file;

import java.nio.file.Path;
import java.util.stream.Stream;

public class ParallelFileReader implements FileReader {
    @Override
    public Stream<String> read(Path path) {
        throw new UnsupportedOperationException();
    }
}
