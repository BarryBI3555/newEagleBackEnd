package com.example.demo.service;

import java.time.LocalDate;

public interface AsyncGeocodeService {
    void doGeocodeAddresses(LocalDate date, String dateStr);
}
