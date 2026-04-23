package com.example.service;

import com.example.exception.ValidationException;
import com.example.model.Barang;
import com.example.model.Kategori;
import com.example.repository.BarangRepository;
import com.example.util.KodeGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventoryService {
    private final BarangRepository repository;

    public InventoryService(BarangRepository repository) {
        this.repository = repository;
    }

    public List<Barang> findAll() throws IOException {
        return repository.findAll();
    }

    public Optional<Barang> findByKode(String kode) throws IOException {
        return repository.findByKode(kode);
    }

    public List<Barang> search(String keyword) throws IOException {
        if (keyword == null || keyword.isBlank()) return findAll();
        return repository.search(keyword);
    }

    public List<Barang> filterByKategori(Kategori kategori) throws IOException {
        if (kategori == null) return findAll();
        return findAll().stream()
                .filter(b -> b.getKategori() == kategori)
                .collect(Collectors.toList());
    }

    public String generateNextKode() throws IOException {
        List<String> kodes = findAll().stream().map(Barang::getKode).collect(Collectors.toList());
        return KodeGenerator.generateBarangKode(kodes);
    }

    public void create(Barang barang) throws ValidationException, IOException {
        validate(barang, true);
        List<Barang> all = new ArrayList<>(findAll());
        all.add(barang);
        repository.saveAll(all);
    }

    public void update(Barang barang) throws ValidationException, IOException {
        validate(barang, false);
        List<Barang> all = new ArrayList<>(findAll());
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getKode().equals(barang.getKode())) {
                all.set(i, barang);
                repository.saveAll(all);
                return;
            }
        }
        throw new ValidationException("Barang dengan kode " + barang.getKode() + " tidak ditemukan");
    }

    public void delete(String kode) throws IOException {
        List<Barang> all = new ArrayList<>(findAll());
        all.removeIf(b -> b.getKode().equals(kode));
        repository.saveAll(all);
    }

    private void validate(Barang b, boolean isCreate) throws ValidationException, IOException {
        if (b.getKode() == null || b.getKode().isBlank())
            throw new ValidationException("Kode wajib diisi");
        if (b.getNama() == null || b.getNama().isBlank())
            throw new ValidationException("Nama wajib diisi");
        if (b.getKategori() == null)
            throw new ValidationException("Kategori wajib dipilih");
        if (b.getUkuran() == null)
            throw new ValidationException("Ukuran wajib dipilih");
        if (b.getKondisi() == null)
            throw new ValidationException("Kondisi wajib dipilih");
        if (b.getHargaBeli() < 0)
            throw new ValidationException("Harga beli tidak boleh negatif");
        if (b.getHargaJual() <= 0)
            throw new ValidationException("Harga jual harus lebih dari 0");
        if (b.getStok() < 0)
            throw new ValidationException("Stok tidak boleh negatif");
        if (isCreate) {
            if (findByKode(b.getKode()).isPresent())
                throw new ValidationException("Kode " + b.getKode() + " sudah dipakai");
        }
    }

    public void reload() throws IOException {
        repository.reload();
    }
}
