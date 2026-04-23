package com.example.service;

import com.example.exception.StokTidakCukupException;
import com.example.exception.ValidationException;
import com.example.model.Barang;
import com.example.model.ItemTransaksi;
import com.example.model.MetodeBayar;
import com.example.model.Transaksi;
import com.example.repository.BarangRepository;
import com.example.repository.TransaksiRepository;
import com.example.util.KodeGenerator;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KasirService {
    private final BarangRepository barangRepo;
    private final TransaksiRepository trxRepo;

    public KasirService(BarangRepository barangRepo, TransaksiRepository trxRepo) {
        this.barangRepo = barangRepo;
        this.trxRepo = trxRepo;
    }

    public double hitungSubtotal(List<ItemTransaksi> items) {
        return items.stream().mapToDouble(ItemTransaksi::getSubtotal).sum();
    }

    public double hitungPotongan(double subtotal, double diskon, boolean persen) {
        if (persen) return subtotal * diskon / 100.0;
        return diskon;
    }

    public double hitungTotal(double subtotal, double potongan) {
        return Math.max(0, subtotal - potongan);
    }

    public Transaksi buatTransaksi(List<ItemTransaksi> items, double diskon, boolean persen,
                                   double bayar, MetodeBayar metode, String kasirUsername)
            throws ValidationException, IOException {
        if (items == null || items.isEmpty()) {
            throw new ValidationException("Keranjang kosong");
        }
        if (metode == null) {
            throw new ValidationException("Metode bayar wajib dipilih");
        }

        List<Barang> stock = new ArrayList<>(barangRepo.findAll());
        for (ItemTransaksi it : items) {
            Barang b = stock.stream()
                    .filter(x -> x.getKode().equals(it.getKodeBarang()))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException("Barang " + it.getKodeBarang() + " tidak ditemukan"));
            if (b.getStok() < it.getQty()) {
                throw new StokTidakCukupException(b.getNama(), b.getStok());
            }
        }

        double subtotal = hitungSubtotal(items);
        double potongan = hitungPotongan(subtotal, diskon, persen);
        double total = hitungTotal(subtotal, potongan);
        if (metode == MetodeBayar.TUNAI && bayar < total) {
            throw new ValidationException("Uang bayar kurang dari total");
        }

        for (ItemTransaksi it : items) {
            Barang b = stock.stream()
                    .filter(x -> x.getKode().equals(it.getKodeBarang()))
                    .findFirst().orElseThrow();
            b.setStok(b.getStok() - it.getQty());
        }
        barangRepo.saveAll(stock);

        List<String> existingIds = trxRepo.findAll().stream().map(Transaksi::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();
        String id = KodeGenerator.generateTrxId(existingIds, now);
        Transaksi trx = new Transaksi(id, now, kasirUsername, items, diskon, persen, bayar, metode);
        List<Transaksi> all = new ArrayList<>(trxRepo.findAll());
        all.add(trx);
        trxRepo.saveAll(all);
        return trx;
    }
}
