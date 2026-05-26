package com.example.demo.service;

import java.time.LocalDate;

public interface AsyncGeocodeService {
    void asyncGeocodeAddresses(LocalDate date, String dateStr);
}
