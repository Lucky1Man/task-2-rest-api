package org.example.task2restapi.service.impl;

import org.example.task2restapi.service.DateTimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class DateTimeServiceImpl implements DateTimeService {
    @Override
    public LocalDateTime utcNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
