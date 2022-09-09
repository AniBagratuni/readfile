package com.example.readfile;

import com.example.readfile.service.file.FileReader;
import com.example.readfile.service.file.SimpleFileReader;
import com.example.readfile.service.tasks.UniqueIps;
import com.example.readfile.service.tasks.UniqueIpsImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    static final Path PATH = Path.of("readfile/src/main/resources/ips.txt");

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(Level.INFO);
        var expected = generateIps();
        var start = Instant.now();
        FileReader fileReader = new SimpleFileReader();
        UniqueIps task = new UniqueIpsImpl(UniqueIpsImpl.DEFAULT_MAX_THREADS_COUNT, UniqueIpsImpl.DEFAULT_MAX_MEM_SIZE, 10);
        var stream = fileReader.read(PATH);
        var count = task.count(stream);
        var end = Instant.now();
        logger.info("Done! duration = {}, size = {}, expected = {}", Duration.between(start, end), count, expected);

    }

    private static int generateIps() throws IOException {
        final Random random = new Random(1);
        var ips = IntStream.range(0, 5_000_000)
                .boxed()
                .map(i -> random.nextInt())
                .map(Integer::toUnsignedString)
                .toList();

        Files.write(PATH, ips);

        return new HashSet<>(ips).size();
    }
}
