package com.example.readfile.service.tasks;

import com.google.common.collect.Iterators;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UniqueIpsImpl implements UniqueIps {

    private static final Logger logger = LogManager.getLogger(UniqueIpsImpl.class);
    public static final int DEFAULT_MAX_MEM_SIZE = 4_800_000; //chunk =10_000
    public static final int DEFAULT_MAX_THREADS_COUNT = 4;

    private final int maxAllowedTimeInSec;
    private final int maxThreads;
    private final int chunkSize;
    private final ExecutorService es;
    private final Set<String> uniqueIps;


    /**
     * use wisely. Don't want to add validations
     * 3*4(digits)+3(dots) = 15 char => 15*8 bytes = 120 b
     * 120*chunkSize*maxThreads<=maxMemory
     */
    public UniqueIpsImpl(int maxThreads, int maxMemory, int maxAllowedTimeInSec) {
        this.maxThreads = maxThreads;
        this.maxAllowedTimeInSec = maxAllowedTimeInSec;
        this.chunkSize = maxMemory / (maxThreads * 120);
        this.es = Executors.newFixedThreadPool(maxThreads);
        this.uniqueIps = ConcurrentHashMap.newKeySet();
        logger.debug("chunk size {}", chunkSize);
    }

    @Override
    public int count(Stream<String> ips) {
        var iterator = Iterators.partition(ips.iterator(), chunkSize);
        StreamSupport.stream(Spliterators.spliterator(iterator, maxThreads, Spliterator.CONCURRENT), true)
                .forEach(chunk -> es.submit(() -> uniqueIps.addAll(new HashSet<>(chunk))));
        es.shutdown();
        try {
            if (!es.awaitTermination(maxAllowedTimeInSec, TimeUnit.SECONDS)) {
                logger.warn("Couldn't complete the task within {} sec", maxAllowedTimeInSec);
            }
        } catch (InterruptedException e) {
            logger.error("interrupted!", e);
            Thread.currentThread().interrupt();
        }
        var ret = uniqueIps.size();
        uniqueIps.clear();
        return ret;
    }
}
