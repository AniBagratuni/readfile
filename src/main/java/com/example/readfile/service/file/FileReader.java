package com.example.readfile.service.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileReader {
    Stream<String> read(Path path) throws IOException;
}
