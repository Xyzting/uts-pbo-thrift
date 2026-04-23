package com.example.repository;

import com.example.model.Transaksi;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TransaksiRepository extends JsonRepository<Transaksi> {
    public TransaksiRepository() {
        super("transaksi.json", new TypeToken<List<Transaksi>>() {});
    }

    public List<Transaksi> findByDateRange(LocalDate from, LocalDate to) throws IOException {
        var start = from.atStartOfDay();
        var end = to.atTime(LocalTime.MAX);
        return findAll().stream()
                .filter(t -> !t.getTanggal().isBefore(start) && !t.getTanggal().isAfter(end))
                .collect(Collectors.toList());
    }
}
