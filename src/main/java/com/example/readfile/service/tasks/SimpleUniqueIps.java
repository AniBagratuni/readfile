package com.example.readfile.service.tasks;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ag on 09.09.22
 */

public class SimpleUniqueIps implements UniqueIps {
    @Override
    public int count(Stream<String> ips) {
        return ips.collect(Collectors.toSet()).size();
    }
}
