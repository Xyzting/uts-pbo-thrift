package com.example.service;

import com.example.model.ItemTransaksi;
import com.example.model.Transaksi;
import com.example.repository.TransaksiRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LaporanService {
    private final TransaksiRepository repository;

    public LaporanService(TransaksiRepository repository) {
        this.repository = repository;
    }

    public List<Transaksi> filterByRange(LocalDate from, LocalDate to) throws IOException {
        return repository.findByDateRange(from, to);
    }

    public double totalSales(List<Transaksi> list) {
        return list.stream().mapToDouble(Transaksi::getTotal).sum();
    }

    public double totalProfit(List<Transaksi> list) {
        return list.stream().mapToDouble(Transaksi::totalProfit).sum();
    }

    public int jumlahTransaksi(List<Transaksi> list) {
        return list.size();
    }

    public Optional<String> bestSeller(List<Transaksi> list) {
        Map<String, Integer> counter = new HashMap<>();
        for (Transaksi t : list) {
            for (ItemTransaksi it : t.getItems()) {
                counter.merge(it.getNamaBarang(), it.getQty(), Integer::sum);
            }
        }
        return counter.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }
}
