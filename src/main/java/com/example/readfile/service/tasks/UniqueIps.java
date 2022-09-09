package com.example.readfile.service.tasks;

import java.util.stream.Stream;

public interface UniqueIps {
    int count(Stream<String> ips);

}
