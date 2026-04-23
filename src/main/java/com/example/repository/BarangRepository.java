package com.example.repository;

import com.example.model.Barang;
import com.example.model.Kategori;
import com.example.model.Kondisi;
import com.example.model.Ukuran;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BarangRepository extends JsonRepository<Barang> {
    public BarangRepository() {
        super("barang.json", new TypeToken<List<Barang>>() {});
    }

    @Override
    protected List<Barang> seed() {
        return List.of(
            new Barang("BR-001", "Kemeja Flanel Kotak", Kategori.ATASAN, "Uniqlo", Ukuran.M, Kondisi.LIKE_NEW, 45000, 95000, 1, "placeholder.png"),
            new Barang("BR-002", "Jeans Skinny Dark", Kategori.BAWAHAN, "H&M", Ukuran.L, Kondisi.VERY_GOOD, 40000, 85000, 1, "placeholder.png"),
            new Barang("BR-003", "Jaket Bomber Hitam", Kategori.OUTER, "Zara", Ukuran.L, Kondisi.GOOD, 80000, 185000, 1, "placeholder.png"),
            new Barang("BR-004", "Dress Motif Bunga", Kategori.DRESS, "H&M", Ukuran.M, Kondisi.LIKE_NEW, 55000, 125000, 1, "placeholder.png"),
            new Barang("BR-005", "Topi Baseball Nike", Kategori.AKSESORIS, "Nike", Ukuran.ALL_SIZE, Kondisi.VERY_GOOD, 25000, 65000, 2, "placeholder.png"),
            new Barang("BR-006", "Kaos Polos Navy", Kategori.ATASAN, "Uniqlo", Ukuran.S, Kondisi.LIKE_NEW, 20000, 55000, 1, "placeholder.png"),
            new Barang("BR-007", "Celana Cargo Hijau", Kategori.BAWAHAN, "Adidas", Ukuran.L, Kondisi.GOOD, 60000, 135000, 1, "placeholder.png"),
            new Barang("BR-008", "Hoodie Oversize", Kategori.ATASAN, "Champion", Ukuran.XL, Kondisi.VERY_GOOD, 75000, 165000, 1, "placeholder.png"),
            new Barang("BR-009", "Kemeja Denim Biru", Kategori.ATASAN, "Levi's", Ukuran.M, Kondisi.GOOD, 50000, 110000, 1, "placeholder.png"),
            new Barang("BR-010", "Rok Plisket Hitam", Kategori.BAWAHAN, "Zara", Ukuran.S, Kondisi.LIKE_NEW, 45000, 98000, 1, "placeholder.png"),
            new Barang("BR-011", "Coat Panjang Coklat", Kategori.OUTER, "Uniqlo", Ukuran.L, Kondisi.VERY_GOOD, 120000, 245000, 1, "placeholder.png"),
            new Barang("BR-012", "Dress Midi Polos", Kategori.DRESS, "H&M", Ukuran.M, Kondisi.GOOD, 50000, 115000, 1, "placeholder.png"),
            new Barang("BR-013", "Tas Selempang Canvas", Kategori.AKSESORIS, "Bershka", Ukuran.ALL_SIZE, Kondisi.VERY_GOOD, 35000, 78000, 1, "placeholder.png"),
            new Barang("BR-014", "Sweater Rajut Krem", Kategori.ATASAN, "Uniqlo", Ukuran.L, Kondisi.LIKE_NEW, 65000, 145000, 1, "placeholder.png"),
            new Barang("BR-015", "Celana Chino Khaki", Kategori.BAWAHAN, "Dockers", Ukuran.M, Kondisi.GOOD, 45000, 98000, 1, "placeholder.png")
        );
    }

    public Optional<Barang> findByKode(String kode) throws IOException {
        return findAll().stream()
                .filter(b -> b.getKode().equals(kode))
                .findFirst();
    }

    public List<Barang> search(String keyword) throws IOException {
        String lc = keyword == null ? "" : keyword.toLowerCase();
        return findAll().stream()
                .filter(b ->
                    b.getNama().toLowerCase().contains(lc) ||
                    b.getBrand().toLowerCase().contains(lc) ||
                    b.getKode().toLowerCase().contains(lc))
                .collect(Collectors.toList());
    }
}
